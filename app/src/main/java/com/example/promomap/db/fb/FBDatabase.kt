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
import com.google.firebase.firestore.Query

class FBDatabase {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    // Retorna o usuário atual
    fun getLoggedUser(): Flow<User?> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val listener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    // Certifique-se de que os nomes em vermelho abaixo são IGUAIS aos do seu console Firebase
                    val user = User(
                        name = snapshot.getString("name") ?: "",
                        cpf = snapshot.getString("cpf") ?: "",   // O segundo campo deve ser o CPF
                        email = snapshot.getString("email") ?: "" // O terceiro campo deve ser o Email
                    )
                    trySend(user)
                }
            }
        awaitClose { listener.remove() }
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

    suspend fun updateUserName(newName: String) {
        val uid = auth.currentUser?.uid ?: return
        try {
            db.collection("users").document(uid)
                .update("name", newName)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getVisitHistory(): Flow<List<Promo>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener = db.collection("users").document(uid)
            .collection("historico")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val history = snapshot?.documents?.mapNotNull { doc ->
                    val fbPromo = doc.toObject(FBPromo::class.java)
                    fbPromo?.id = doc.id
                    fbPromo?.toPromo()
                } ?: emptyList()
                trySend(history)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addToHistory(promo: Promo) {
        val uid = auth.currentUser?.uid ?: return
        val promoMap = hashMapOf(
            "item" to promo.item,
            "marca" to promo.marca,
            "preco" to promo.preco,
            "lat" to promo.localizacao?.latitude,
            "lng" to promo.localizacao?.longitude,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        try {
            db.collection("users").document(uid)
                .collection("historico")
                .add(promoMap)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 1. Notificações
    suspend fun updateNotificationSetting(key: String, enabled: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("settings.$key", enabled).await() // Salva como um sub-objeto settings
    }

    // 2. Favoritos
    suspend fun addFavorite(productName: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("favorites", com.google.firebase.firestore.FieldValue.arrayUnion(productName)).await()
    }
    suspend fun removeFavorite(productName: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("favorites", com.google.firebase.firestore.FieldValue.arrayRemove(productName)).await()
    }
    fun getFavorites(): Flow<List<String>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val favorites = snapshot?.get("favorites") as? List<String> ?: emptyList()
                trySend(favorites)
            }
        awaitClose { listener.remove() }
    }

    // 3. Locais
    suspend fun saveLocation(name: String, address: String, radius: String, lat: Double, lng: Double) {
        val uid = auth.currentUser?.uid ?: return
        val locMap = hashMapOf("name" to name, "address" to address, "radius" to radius, "lat" to lat.toString(), "lng" to lng.toString())
        db.collection("users").document(uid)
            .update("savedLocations", com.google.firebase.firestore.FieldValue.arrayUnion(locMap)).await()
    }

    // Ler locais em tempo real
    fun getSavedLocations(): Flow<List<Map<String, String>>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                // Pega a lista de mapas do Firestore
                val locs = snapshot?.get("savedLocations") as? List<Map<String, String>> ?: emptyList()
                trySend(locs)
            }
        awaitClose { listener.remove() }
    }

    // Remover um local
    suspend fun removeLocation(locMap: Map<String, String>) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("savedLocations", com.google.firebase.firestore.FieldValue.arrayRemove(locMap)).await()
    }

    // Puxa as promoções uma única vez para o Worker
    suspend fun getPromosOnce(): List<Promo> {
        return try {
            val snapshot = db.collection("promocoes").get().await()
            snapshot.documents.mapNotNull { doc ->
                val fbPromo = doc.toObject(FBPromo::class.java)
                fbPromo?.id = doc.id
                fbPromo?.toPromo()
            }
        } catch (e: Exception) { emptyList() }
    }

    // Puxa os dados do usuário (Favoritos e Locais) uma única vez
    suspend fun getUserSettingsOnce(): Map<String, Any>? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.data
        } catch (e: Exception) { null }
    }
}