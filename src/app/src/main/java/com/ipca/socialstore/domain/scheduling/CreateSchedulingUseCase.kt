package com.ipca.socialstore.domain.scheduling

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.NotificationSchedulingModel
import com.ipca.socialstore.data.models.SchedulingDateModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.ExistBeneficiaryUseCase
import com.ipca.socialstore.domain.notificationScheduling.CreateNotificationUseCase
import com.ipca.socialstore.domain.schedulingDate.CreateSchedulingDateCreateUseCase
import javax.inject.Inject

class CreateSchedulingUseCase @Inject constructor(
    private val existBeneficiaryUseCase: ExistBeneficiaryUseCase,
    private val createSchedulingDateCreateUseCase: CreateSchedulingDateCreateUseCase,
    private val createNotificationUseCase: CreateNotificationUseCase,
    private val schedulingRepository: SchedulingRepository,
    private val exceptionMapper: ExceptionMapper,
){
    suspend operator fun invoke(beneficiaryId : String, schedulingDate : SchedulingDateModel, notification : NotificationSchedulingModel): ResultWrapper<Int> {

        return try {
            //Verificar se o Benef existe
            val beneficiaryResult = existBeneficiaryUseCase(beneficiaryId)
            if (beneficiaryResult is ResultWrapper.Error)
                return ResultWrapper.Error(beneficiaryResult.error)
            val beneficiaryId = (beneficiaryResult as ResultWrapper.Success).data

            //Criar data e ver se ja nao tem datas agendadas
            val schedulingDateResult = createSchedulingDateCreateUseCase(schedulingDate)
            if (schedulingDateResult is ResultWrapper.Error)
                return ResultWrapper.Error(schedulingDateResult.error)
            val schedulingDateId = (schedulingDateResult as ResultWrapper.Success).data

            //Criar Notificacao
            val createNotificationResult = createNotificationUseCase(notification)
            if (createNotificationResult is ResultWrapper.Error)
                return ResultWrapper.Error(createNotificationResult.error)
            val notificationId = (createNotificationResult as ResultWrapper.Success).data

            //Associar ao Benef

            val newScheduling = SchedulingModel(
                dateId = schedulingDateId,
                notificationId = notificationId,
                beneficiaryId = beneficiaryId
            )
            val createSchedulingResult = schedulingRepository.createScheduling(newScheduling)
            if (createSchedulingResult is ResultWrapper.Error)
                return ResultWrapper.Error(createSchedulingResult.error)
            val schedulingId = (createSchedulingResult as ResultWrapper.Success).data
            ResultWrapper.Success(schedulingId)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }

    }
}