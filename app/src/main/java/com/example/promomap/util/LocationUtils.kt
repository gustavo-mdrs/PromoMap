package com.example.promomap.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.Locale

// --- 1. CONFIGURAÇÃO DA API VIA CEP ---
data class ViaCepResponse(
    val logradouro: String?,
    val bairro: String?,
    val localidade: String?,
    val uf: String?
)

interface ViaCepApi {
    @GET("{cep}/json/")
    suspend fun buscarCep(@Path("cep") cep: String): ViaCepResponse
}

object ViaCepClient {
    val api: ViaCepApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepApi::class.java)
    }
}

// --- 2. FUNÇÕES ÚTEIS DE LOCALIZAÇÃO ---
object LocationUtils {

    // Converte um endereço de texto em Latitude e Longitude (usando o Android)
    suspend fun obterCoordenadasPorEndereco(context: Context, endereco: String): LatLng? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(endereco, 1)
                if (!addresses.isNullOrEmpty()) {
                    LatLng(addresses[0].latitude, addresses[0].longitude)
                } else null
            } catch (e: Exception) { null }
        }
    }

    // Pega a localização GPS atual de forma segura e leve
    @SuppressLint("MissingPermission")
    suspend fun obterLocalizacaoAtual(context: Context): LatLng? {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location = fusedLocationClient.lastLocation.await() // Versão segura

            if (location != null) LatLng(location.latitude, location.longitude) else null
        } catch (e: Exception) {
            null
        }
    }
}