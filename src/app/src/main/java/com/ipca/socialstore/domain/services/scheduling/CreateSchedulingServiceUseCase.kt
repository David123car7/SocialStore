package com.ipca.socialstore.domain.services.scheduling

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.ExistBeneficiaryUseCase
import com.ipca.socialstore.domain.notificationSchedule.CreateInitialNotificationUseCase
import com.ipca.socialstore.domain.scheduling.CreateSchedulingUseCase
import javax.inject.Inject

class CreateSchedulingServiceUseCase @Inject constructor(
    private val createSchedulingUseCase: CreateSchedulingUseCase,
    private val createInitialNotificationUseCase: CreateInitialNotificationUseCase,
    private val exceptionMapper: ExceptionMapper,
){
    suspend operator fun invoke(scheduling : SchedulingModel): ResultWrapper<SchedulingModel> {

        return try {

            val newScheduling = SchedulingModel(
                schedulingDate = scheduling.schedulingDate,
                beneficiaryId = scheduling.beneficiaryId,
                state = "in_Progress",
                reason = null,
                note = null
            )
            val schedulingResult = createSchedulingUseCase(newScheduling)
            if (schedulingResult is ResultWrapper.Error)
                return ResultWrapper.Error(schedulingResult.error)
            val scheduling = (schedulingResult as ResultWrapper.Success).data

            val notification = NotificationScheduledModel(
                subject = "Tem uma entrega agendada para o ${scheduling.schedulingDate}",
                isRead = false,
                title = "Novo Agendamento"
            )

            val createNotificationResult = createInitialNotificationUseCase(notification)
            if (createNotificationResult is ResultWrapper.Error)
                return ResultWrapper.Error(createNotificationResult.error)
            val notificationId = (createNotificationResult as ResultWrapper.Success).data

            ResultWrapper.Success(scheduling)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }

    }
}