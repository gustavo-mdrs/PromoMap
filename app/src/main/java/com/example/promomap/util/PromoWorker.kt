package com.example.promomap.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
// import com.google.android.gms.maps.model.LatLng
// import com.example.promomap.db.fb.FBDatabase

class PromoWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val temPromocaoPerto = true // Substitua pela sua lógica de getAlerts

            if (temPromocaoPerto) {
                NotificationHelper.showNotification(
                    context = applicationContext, // O Worker tem seu próprio contexto seguro
                    title = "Promoção Encontrada!",
                    content = "Um dos seus produtos favoritos está com desconto perto de você."
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}