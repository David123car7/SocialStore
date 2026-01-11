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
) {
    suspend operator fun invoke(scheduling: SchedulingModel): ResultWrapper<SchedulingModel> {
        return try {
            // 1. Preparar o objeto para a BD
            val newScheduling = SchedulingModel(
                schedulingDate = scheduling.schedulingDate,
                beneficiaryId = scheduling.beneficiaryId,
                state = "in_Progress",
                reason = null,
                note = null,
                notifiedAdmin = false
            )

            val schedulingResult = createSchedulingUseCase(newScheduling)


            val createdScheduling = when (schedulingResult) {
                is ResultWrapper.Success -> schedulingResult.data
                is ResultWrapper.Error -> return ResultWrapper.Error(schedulingResult.error)
            }

            println("Agendamento criado com ID: ${createdScheduling.id}")

            val uniqueKey = "${createdScheduling.id}_${createdScheduling.schedulingDate}"
            val notification = NotificationScheduledModel(
                subject = "Tem uma entrega agendada para o ${createdScheduling.schedulingDate}",
                isRead = false,
                title = "Novo Agendamento",
                notificationKey = uniqueKey,
                beneficiaryId = createdScheduling.beneficiaryId
            )

            val createNotificationResult = createInitialNotificationUseCase(notification)

            if (createNotificationResult is ResultWrapper.Error) {
                println("Erro ao criar notificação: ${createNotificationResult.error}")
            }

            ResultWrapper.Success(createdScheduling)

        } catch (e: Exception) {
            println("ERRO NO TRY: ${e.message}")
            e.printStackTrace()
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}