package com.ipca.socialstore.domain.services

import androidx.room.util.copy
import com.ipca.socialstore.data.models.DeliveryItemsModel
import com.ipca.socialstore.data.repository.DeliveryItemsRepository
import com.ipca.socialstore.data.repository.DeliveryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.delivery.CreateDeliveryUseCase
import com.ipca.socialstore.domain.deliveryItems.CreateDeliveryItemsUseCase
import com.ipca.socialstore.domain.deliveryItems.GetDeliveryItemsBySchedulingIdUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByIdUseCase
import javax.inject.Inject

class CreateDeliveryServiceUseCase @Inject constructor(
    private val createDeliveryItemsUseCase: CreateDeliveryItemsUseCase,
    private val createDeliveryUseCase: CreateDeliveryUseCase,
    private val getDeliveryItemsBySchedulingIdUseCase: GetDeliveryItemsBySchedulingIdUseCase,
    private val deliveryItemsRepository: DeliveryItemsRepository
){

    suspend operator fun invoke(schedulingId : Int,  stockMap : Map<Int, Int>) : ResultWrapper<List<DeliveryItemsModel>>{

        val deliveryResult = createDeliveryUseCase(schedulingId)
        if (deliveryResult is ResultWrapper.Error) return ResultWrapper.Error(deliveryResult.error)

        val deliveryId = (deliveryResult as ResultWrapper.Success).data

        val itemsResult = getDeliveryItemsBySchedulingIdUseCase(deliveryId)

        val itemsToInsert = mutableListOf<DeliveryItemsModel>()
        val itemsToUpdate = mutableListOf<DeliveryItemsModel>()
        val itemsToDelete = mutableListOf<Int>()
        if (itemsResult is ResultWrapper.Success){
            val dbItems = itemsResult.data

            stockMap.forEach { (stockId, qty) ->
                val match = dbItems.find { it.stockId == stockId }
                if (match == null){
                    itemsToInsert.add(DeliveryItemsModel(deliveryId = deliveryId, stockId = stockId, quantity = qty))
                }
                else if (match.quantity != qty){
                    itemsToUpdate.add(match.copy(quantity = qty))
                }
            }

            dbItems.forEach { dbItems ->
                if(!stockMap.contains(dbItems.stockId)){
                    dbItems.id.let {itemsToDelete.add(it!!)  }
                }
            }
        }else{
            stockMap.forEach { (id, qty) ->
                itemsToInsert.add(DeliveryItemsModel(deliveryId = deliveryId, stockId = id, quantity = qty))
            }
        }
        if (itemsResult is ResultWrapper.Error) return ResultWrapper.Error(itemsResult.error)

        if (itemsToInsert.isNotEmpty()) deliveryItemsRepository.createDeliveryItems(itemsToInsert)
        if (itemsToUpdate.isNotEmpty()) deliveryItemsRepository.updateDeliveryItems(itemsToUpdate)
        if (itemsToDelete.isNotEmpty()) deliveryItemsRepository.deleteDeliveryItems(itemsToDelete)

        val finalItems = itemsToInsert + itemsToUpdate
        return ResultWrapper.Success(finalItems)
    }
}

