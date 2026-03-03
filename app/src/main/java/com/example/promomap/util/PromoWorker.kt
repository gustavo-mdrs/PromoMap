package com.example.promomap.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.promomap.db.fb.FBDatabase
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class PromoWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = FBDatabase()


            val allPromos = db.getPromosOnce()
            val userSettings = db.getUserSettingsOnce() ?: return Result.success()

            val favorites = userSettings["favorites"] as? List<String> ?: emptyList()
            val savedLocations = userSettings["savedLocations"] as? List<Map<String, String>> ?: emptyList()

            val settings = userSettings["settings"] as? Map<String, Boolean> ?: emptyMap()
            val notifFav = settings["produto_fav"] ?: true
            val notifFinalizada = settings["finalizada"] ?: true
            val notifLocaisSalvos = settings["promo_proxima"] ?: true
            val notifAltaDensidade = settings["alta_densidade"] ?: true
            val notifLocalAtual = settings["promo_local_atual"] ?: true

            var currentLocation: LatLng? = null
            if (notifLocalAtual) {

                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
                        val loc = fusedLocationClient.lastLocation.await() // Pega a última localização do cache do Android
                        if (loc != null) {
                            currentLocation = LatLng(loc.latitude, loc.longitude)
                        }
                    } catch (e: Exception) {

                    }
                }
            }

            var promocoesProximasCount = 0


            for (promo in allPromos) {
                val promoLatLng = promo.localizacao ?: continue

                // REGRA 1: Favoritos
                if (notifFav && favorites.any { it.equals(promo.item, ignoreCase = true) }) {
                    NotificationHelper.showNotification(applicationContext, "Produto Favorito!", "${promo.item} está em oferta por R$ ${promo.preco}!")
                }

                // REGRA 2: Promoções Finalizadas
                if (notifFinalizada && (promo.status == "Finalizada" || promo.status == "Encerrada")) {
                    NotificationHelper.showNotification(applicationContext, "Promoção Encerrada", "A oferta de ${promo.item} acabou.")
                }

                // REGRA 3: GPS Atual
                if (notifLocalAtual && currentLocation != null) {
                    val distGPS = calculateDistance(currentLocation.latitude, currentLocation.longitude, promoLatLng.latitude, promoLatLng.longitude)
                    if (distGPS <= 3.0) { // Raio de 3km da localização atual do usuário
                        NotificationHelper.showNotification(applicationContext, "Oferta perto de você!", "${promo.item} a apenas ${String.format("%.1f", distGPS)}km de você.")
                    }
                }

                // REGRA 4 e 5: Locais Salvos e Alta Densidade
                savedLocations.forEach { loc ->
                    val raioSalvo = loc["radius"]?.toDoubleOrNull() ?: 5.0
                    val isProximo = calculateDistance(loc["lat"]?.toDouble() ?: 0.0, loc["lng"]?.toDouble() ?: 0.0, promoLatLng.latitude, promoLatLng.longitude) <= raioSalvo

                    if (isProximo) {
                        promocoesProximasCount++
                        if (notifLocaisSalvos) {
                            NotificationHelper.showNotification(applicationContext, "Oferta perto de ${loc["name"]}", "${promo.item} no valor de R$ ${promo.preco}.")
                        }
                    }
                }
            }

            // REGRA 5: Alta Densidade (Gatilho fora do For)
            if (notifAltaDensidade && promocoesProximasCount >= 5) {
                NotificationHelper.showNotification(applicationContext, "Área Quente!", "Existem $promocoesProximasCount promoções ativas perto dos seus locais salvos!")
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        if (lat1 == 0.0 || lon1 == 0.0) return 9999.0
        val r = 6371 // Raio da terra em km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}