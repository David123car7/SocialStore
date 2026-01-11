package com.ipca.socialstore.Work.scheduling

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminSchedulingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val workManager = WorkManager.getInstance(context)

    fun startAdminMonitoring() {
        // Constraints: Só corre se houver internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AdminScheduleWorker>()
            .setConstraints(constraints)
            .addTag("ADMIN_MONITOR_TAG")
            .build()

        workManager.enqueueUniqueWork(
            "admin_schedule_monitor",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    fun stopAdminMonitoring() {
        workManager.cancelUniqueWork("admin_schedule_monitor")
    }
}