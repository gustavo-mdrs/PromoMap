package com.example.promomap.db.fb

import com.example.promomap.model.Promo
import com.example.promomap.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FBDatabase {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Retorna o usuário atual
    fun getCurrentUser(): User? {
        val fbUser = auth.currentUser
        // Nota: Para simplificar, retornamos um User básico.
        // Se precisar do CPF, teria que buscar no Firestore, mas para Auth basta isso por enquanto.
        return if (fbUser != null) User(fbUser.displayName ?: "", "", fbUser.email ?: "") else null
    }

    // --- O GRANDE TRUNFO: FLOW EM VEZ DE LISTENER ---
    // Essa função cria um "canal" de dados em tempo real
    fun getPromos(): Flow<List<Promo>> = callbackFlow {
        // Referência à coleção
        val collection = db.collection("promocoes")

        // Adiciona o ouvinte do Firestore
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Fecha o canal se der erro
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Converte cada documento do Firebase (FBPromo) para nosso modelo (Promo)
                val promos = snapshot.documents.mapNotNull { doc ->
                    val fbPromo = doc.toObject(FBPromo::class.java)
                    fbPromo?.id = doc.id // Garante que o ID do doc está no objeto
                    fbPromo?.toPromo()
                }
                // "Emite" a lista nova para o app
                trySend(promos)
            }
        }

        // Quando a tela fecha ou o ViewModel morre, isso remove o listener do Firebase para economizar bateria
        awaitClose { listener.remove() }
    }

    // Funções Suspend (Rodam em Background)
    suspend fun addPromo(promo: FBPromo) {
        // Se não tiver ID, deixa o Firestore gerar
        if (promo.id.isNullOrEmpty()) {
            db.collection("promocoes").add(promo).await()
        } else {
            db.collection("promocoes").document(promo.id!!).set(promo).await()
        }
    }

    suspend fun removePromo(promoId: String) {
        db.collection("promocoes").document(promoId).delete().await()
    }

    // Mantivemos o register igual, pois é uma ação única
    fun register(user: FBUser) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).set(user)
    }
}