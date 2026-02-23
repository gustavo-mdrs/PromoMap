package com.example.promomap.repo

import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.db.fb.toFBPromo
import com.example.promomap.model.Promo
import com.example.promomap.model.User
import kotlinx.coroutines.flow.Flow

class PromoRepository(private val fbDatabase: FBDatabase) {

    // Apenas repassa o fluxo de dados que vem do banco
    val promos: Flow<List<Promo>> = fbDatabase.getPromos()

    suspend fun add(promo: Promo) {
        // Converte para o formato do Firebase antes de salvar
        fbDatabase.addPromo(promo.toFBPromo())
    }

    suspend fun remove(promo: Promo) {
        if (!promo.id.isNullOrEmpty()) {
            fbDatabase.removePromo(promo.id)
        }
    }

    fun getLoggedUser(): Flow<User?> = fbDatabase.getLoggedUser()
}