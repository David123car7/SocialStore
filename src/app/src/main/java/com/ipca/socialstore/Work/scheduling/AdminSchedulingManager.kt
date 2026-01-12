package com.ipca.socialstore.Work.scheduling

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ipca.socialstore.Work.ExpirationDateWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminSchedulingManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val ADMIN_MONITOR_TAG = "ADMIN_MONITOR_TAG"
        const val TAG_SYNC = "admin_schedule_monitor"
    }

    private val workManager = WorkManager.getInstance(context)

    fun startAdminMonitoring() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AdminScheduleWorker>()
            .setConstraints(constraints)
            .addTag(ADMIN_MONITOR_TAG)
            .build()

        workManager.enqueueUniqueWork(
            TAG_SYNC,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    fun stopAdminMonitoring() {
        workManager.cancelUniqueWork("admin_schedule_monitor")
    }
}