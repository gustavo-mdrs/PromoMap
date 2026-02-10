package com.example.promomap.model

import com.google.android.gms.maps.model.LatLng

data class Promo(
    val id: String? = null,
    val item: String,
    val marca: String,
    val preco: Double,
    val localizacao: LatLng? = null,
    val status: String = "Ativa"
)