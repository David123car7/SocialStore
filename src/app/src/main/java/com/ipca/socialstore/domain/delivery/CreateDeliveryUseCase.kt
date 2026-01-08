package com.ipca.socialstore.domain.delivery

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.DeliveriesModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.repository.DeliveryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateDeliveryUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(schedulingId: Int): ResultWrapper<Int> {
        val checkResult = deliveryRepository.getDeliveryBySchedulingId(schedulingId)

        return when (checkResult) {
            is ResultWrapper.Success -> {
                ResultWrapper.Success(checkResult.data.id!!)
            }
            is ResultWrapper.Error -> {
                if (checkResult.error == AppError.DataNotFound) {
                    deliveryRepository.createDelivery(schedulingId)
                } else {
                    ResultWrapper.Error(checkResult.error)
                }
            }
        }
    }
}