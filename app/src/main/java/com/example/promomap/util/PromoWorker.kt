package com.example.promomap.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.promomap.db.fb.FBDatabase
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class PromoWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val db = FBDatabase()

            // 1. Pega os dados
            val allPromos = db.getPromosOnce()
            val userSettings = db.getUserSettingsOnce() ?: return Result.success()

            // Extrai as listas do Firestore de forma segura
            val favorites = userSettings["favorites"] as? List<String> ?: emptyList()
            val savedLocations = userSettings["savedLocations"] as? List<Map<String, String>> ?: emptyList()

            // 2. Aplica as 4 Regras de Notificação
            var promocoesProximasCount = 0

            for (promo in allPromos) {
                val promoLatLng = promo.localizacao ?: continue

                // REGRA 1: Produtos de Interesse (Favoritos)
                if (favorites.any { it.equals(promo.item, ignoreCase = true) }) {
                    NotificationHelper.showNotification(applicationContext, "Produto Favorito!", "${promo.item} está em oferta por R$ ${promo.preco}!")
                }

                // REGRA 4: Promoções Finalizadas
                if (promo.status == "Finalizada" || promo.status == "Encerrada") {
                    NotificationHelper.showNotification(applicationContext, "Promoção Encerrada", "A oferta de ${promo.item} acabou.")
                }

                // Verifica distância para os Locais Salvos (REGRA 2 e 3)
                savedLocations.forEach { loc ->
                    val raioSalvo = loc["radius"]?.toDoubleOrNull() ?: 5.0
                    val isProximo = calculateDistance(loc["lat"]?.toDouble() ?: 0.0, loc["lng"]?.toDouble() ?: 0.0, promoLatLng.latitude, promoLatLng.longitude) <= raioSalvo

                    if (isProximo) {
                        promocoesProximasCount++
                        // REGRA 2: Promoções Próximas
                        NotificationHelper.showNotification(applicationContext, "Oferta perto de ${loc["name"]}", "${promo.item} no valor de R$ ${promo.preco}.")
                    }
                }
            }

            // REGRA 3: Alta Densidade
            if (promocoesProximasCount >= 5) {
                NotificationHelper.showNotification(applicationContext, "Área Quente!", "Existem $promocoesProximasCount promoções ativas perto dos seus locais salvos!")
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    // Fórmula de Haversine para calcular distância em KM
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        if (lat1 == 0.0 || lon1 == 0.0) return 9999.0 // Evita cálculo se local salvo não tiver coordenada
        val r = 6371 // Raio da terra em km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}