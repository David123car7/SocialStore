package com.ipca.socialstore.Work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.notification.WorkerExpirationDateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class ExpirationDateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workerExpirationDateUseCase: WorkerExpirationDateUseCase
) : CoroutineWorker(appContext, workerParams) {

    private val CHANNEL_ID = "stock_notifications"

    override suspend fun doWork(): Result {
        Log.d("WORKER_TEST", "Worker arrancou!")

        return when (val result = workerExpirationDateUseCase()) {
            is ResultWrapper.Success -> {
                val ids = result.data
                if (ids.isNotEmpty()) {
                    Log.d("WORKER_TEST", "Encontrados ${ids.size} itens a expirar: $ids")
                    showNotification(ids.size)
                } else {
                    Log.d("WORKER_TEST", "Nenhum item expira nos próximos 20 dias.")
                }
                Result.success()
            }
            is ResultWrapper.Error -> {
                Log.e("WORKER_TEST", "Erro na busca: ${result.error}")
                Result.retry() // Tenta novamente se for erro de rede
            }
        }
    }

    private fun showNotification(count: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Criar o Canal (Obrigatório para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alertas de Validade",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifica quando produtos estão prestes a expirar"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Construir a Notificação
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Substitui pelo teu ícone
            .setContentTitle("Atenção ao Stock!")
            .setContentText("Tens $count produtos a expirar nos próximos 20 dias.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // 3. Disparar
        notificationManager.notify(1, builder.build())
    }
}