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

        val syncRequest = PeriodicWorkRequestBuilder<ExpirationDateWorker>(24, TimeUnit.HOURS)
            .addTag(TAG_SYNC)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_SYNC_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        val testRequest = OneTimeWorkRequestBuilder<ExpirationDateWorker>()
            .addTag("TEST_NOW")
            .build()

        workManager.enqueue(testRequest)
    }
}