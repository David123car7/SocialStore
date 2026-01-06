package com.ipca.socialstore.domain.delivery

import com.ipca.socialstore.data.models.DeliveriesModel
import com.ipca.socialstore.data.repository.DeliveryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetDeliveryBySchedulingIdUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
){
    suspend operator fun invoke(schedulingId : Int) : ResultWrapper<DeliveriesModel>{
        return deliveryRepository.getDeliveryBySchedulingId(schedulingId)
    }
}