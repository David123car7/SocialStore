package com.ipca.socialstore.Work.scheduling

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.CreateStockNotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class AdminScheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: SchedulingRepository,
    private val notificationService: CreateStockNotificationService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!notificationService.checkIfUserIsAdmin()) return Result.success()

        while (!isStopped) {
            val result = repository.getUnnotifiedSchedules()

            if (result is ResultWrapper.Success && result.data.isNotEmpty()) {
                result.data.forEach { schedule ->
                    notificationService.showSystemNotification(
                        "Alteração de Agendamento",
                        "O agendamento #${schedule.id} foi ${schedule.state}."
                    )

                    repository.markAsNotified(schedule.id!!)
                }
            }

            delay(30_000)
        }

        return Result.success()
    }
}