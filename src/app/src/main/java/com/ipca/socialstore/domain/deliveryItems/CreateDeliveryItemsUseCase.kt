package com.ipca.socialstore.domain.deliveryItems

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.DeliveryItemsModel
import com.ipca.socialstore.data.repository.DeliveryItemsRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateDeliveryItemsUseCase @Inject constructor(
    private val deliveryItemsRepository: DeliveryItemsRepository,
    private val getDeliveryItemsBySchedulingIdUseCase: GetDeliveryItemsBySchedulingIdUseCase
) {
    suspend operator fun invoke(deliveryItems: List<DeliveryItemsModel>, schedulingId: Int): ResultWrapper<Int> {
        val getDeliveryItemsResult = getDeliveryItemsBySchedulingIdUseCase(schedulingId)

        if (getDeliveryItemsResult is ResultWrapper.Error) {
            return if (getDeliveryItemsResult.error == AppError.DataNotFound) {
                val createResult = deliveryItemsRepository.createDeliveryItems(deliveryItems)

                if (createResult is ResultWrapper.Success) {
                    ResultWrapper.Success(deliveryItems.first().deliveryId)
                } else {
                    ResultWrapper.Error((createResult as ResultWrapper.Error).error)
                }
            } else {
                ResultWrapper.Error(getDeliveryItemsResult.error)
            }
        }

        val existingItems = (getDeliveryItemsResult as ResultWrapper.Success).data

        return ResultWrapper.Success(existingItems.first().deliveryId)
    }
}