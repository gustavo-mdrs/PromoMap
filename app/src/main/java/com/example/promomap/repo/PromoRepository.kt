package com.example.promomap.repo

import com.example.promomap.db.fb.FBDatabase
import com.example.promomap.db.fb.toFBPromo
import com.example.promomap.model.Promo
import kotlinx.coroutines.flow.Flow

class PromoRepository(private val fbDatabase: FBDatabase) {

    // Escuta todas as promoções para exibir no mapa [cite: 569, 588]
    val promos: Flow<List<Promo>> = fbDatabase.getPromos()

    // Adiciona uma nova promoção ao banco global [cite: 573, 588]
    suspend fun add(promo: Promo) {
        fbDatabase.addPromo(promo.toFBPromo())
    }

    // Remove uma promoção (ex: informar que acabou) [cite: 573, 588]
    suspend fun remove(promo: Promo) {
        if (!promo.id.isNullOrEmpty()) {
            fbDatabase.removePromo(promo.id)
        }
    }
}