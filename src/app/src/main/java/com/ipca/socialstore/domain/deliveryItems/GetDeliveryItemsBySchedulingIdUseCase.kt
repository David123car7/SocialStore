package com.ipca.socialstore.domain.deliveryItems

import com.ipca.socialstore.data.models.DeliveryItemsModel
import com.ipca.socialstore.data.repository.DeliveryItemsRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetDeliveryItemsBySchedulingIdUseCase @Inject constructor(
    private val deliveryItemsRepository: DeliveryItemsRepository,
) {
    suspend operator fun invoke (delivery : Int): ResultWrapper<List<DeliveryItemsModel>>{
        return deliveryItemsRepository.getDeliveryItemsByDeliveryId(delivery)
    }
}