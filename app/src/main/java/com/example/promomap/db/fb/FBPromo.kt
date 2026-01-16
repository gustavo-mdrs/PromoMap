package com.example.promomap.db.fb

import com.example.promomap.model.Promo
import com.google.android.gms.maps.model.LatLng

class FBPromo {
    var id: String? = null
    var item: String? = null
    var marca: String? = null
    var preco: Double? = null
    var lat: Double? = null
    var lng: Double? = null
    var status: String? = "Ativa"

    // Converte de FBPromo (Firebase) para Promo (Model) [cite: 889]
    fun toPromo(): Promo {
        val latlng = if (lat != null && lng != null) LatLng(lat!!, lng!!) else null
        return Promo(
            id = id,
            item = item!!,
            marca = marca!!,
            preco = preco!!,
            localizacao = latlng,
            status = status ?: "Ativa"
        )
    }
}

// Função de extensão para converter de Promo (Model) para FBPromo (Firebase) [cite: 893]
fun Promo.toFBPromo(): FBPromo {
    val fbPromo = FBPromo()
    fbPromo.id = this.id
    fbPromo.item = this.item
    fbPromo.marca = this.marca
    fbPromo.preco = this.preco
    fbPromo.lat = this.localizacao?.latitude
    fbPromo.lng = this.localizacao?.longitude
    fbPromo.status = this.status
    return fbPromo
}