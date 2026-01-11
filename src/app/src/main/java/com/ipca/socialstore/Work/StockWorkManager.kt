package com.ipca.socialstore.Work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest


import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

class StockWorkManager(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    companion object {
        const val UNIQUE_SYNC_WORK = "expiration_check_work"
        const val TAG_SYNC = "stock_sync_tag"
    }

    fun notificationExpirationDate() {
        val calendar = java.util.Calendar.getInstance()
        val now = calendar.timeInMillis

        val nextTenAM = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 1)
            set(java.util.Calendar.MINUTE, 20)
            set(java.util.Calendar.SECOND, 0)

            if (timeInMillis <= now) {
                add(java.util.Calendar.DAY_OF_MONTH, 1)
            }
        }

        val delay = nextTenAM.timeInMillis - now

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<ExpirationDateWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(TAG_SYNC)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_SYNC_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun testImortalidade() {
        val request = OneTimeWorkRequestBuilder<ExpirationDateWorker>()
            .setInitialDelay(2, TimeUnit.MINUTES) // Dá-te 2 minutos de folga
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .addTag("TESTE_IMORTAL")
            .build()

        workManager.enqueue(request)
    }
}