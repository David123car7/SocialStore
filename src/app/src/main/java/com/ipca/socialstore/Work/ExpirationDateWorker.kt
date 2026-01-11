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
import com.ipca.socialstore.domain.services.CreateStockNotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class ExpirationDateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val workerExpirationDateUseCase: WorkerExpirationDateUseCase,
    private val createStockNotificationService: CreateStockNotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return when (val result = workerExpirationDateUseCase()) {
            is ResultWrapper.Success -> {
                val ids = result.data
                if (ids.isNotEmpty()) {
                    createStockNotificationService(ids)
                } else {
                    Log.d("WORKER_TEST", "Nenhum item expira nos próximos 20 dias.")
                }
                Result.success()
            }
            is ResultWrapper.Error -> {
                Result.retry()
            }
        }
    }

}