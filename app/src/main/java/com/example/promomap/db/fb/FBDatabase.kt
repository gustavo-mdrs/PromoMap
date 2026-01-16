package com.example.promomap.db.fb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onPromoAdded(promo: FBPromo)    // Alterado de City para Promo
        fun onPromoUpdated(promo: FBPromo)  // Alterado de City para Promo
        fun onPromoRemoved(promo: FBPromo)  // Alterado de City para Promo
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var promoListReg: ListenerRegistration? = null // Escuta de promoções
    private var listener : Listener? = null

    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                promoListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }

            // Carrega dados do perfil (Nome, CPF)
            val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
            refCurrUser.get().addOnSuccessListener {
                it.toObject(FBUser::class.java)?.let { user ->
                    listener?.onUserLoaded(user)
                }
            }

            // Monitora a coleção GLOBAL de promoções (para todos verem no mapa)
            promoListReg = db.collection("promocoes")
                .addSnapshotListener { snapshots, ex ->
                    if (ex != null) return@addSnapshotListener
                    snapshots?.documentChanges?.forEach { change ->
                        val fbPromo = change.document.toObject(FBPromo::class.java)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> listener?.onPromoAdded(fbPromo)
                            DocumentChange.Type.MODIFIED -> listener?.onPromoUpdated(fbPromo)
                            DocumentChange.Type.REMOVED -> listener?.onPromoRemoved(fbPromo)
                        }
                    }
                }
        }
    }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }

    // Registra o perfil do usuário no Firestore (Slide 6)
    fun register(user: FBUser) {
        val uid = auth.currentUser?.uid ?: throw RuntimeException("User not logged in!")
        db.collection("users").document(uid).set(user)
    }

    // Adiciona uma nova promoção ao sistema (Slide 8)
    fun addPromo(promo: FBPromo) {
        if (auth.currentUser == null) throw RuntimeException("User not logged in!")
        // Usa o nome do item como ID do documento ou um ID gerado automaticamente
        db.collection("promocoes").document(promo.id ?: promo.item!!).set(promo)
    }

    // Remove ou encerra uma promoção (Slide 10)
    fun removePromo(promo: FBPromo) {
        if (auth.currentUser == null) throw RuntimeException("User not logged in!")
        db.collection("promocoes").document(promo.id ?: promo.item!!).delete()
    }
}