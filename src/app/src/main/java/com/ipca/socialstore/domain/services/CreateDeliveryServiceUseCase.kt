package com.ipca.socialstore.domain.services

import androidx.room.util.copy
import com.ipca.socialstore.data.models.DeliveryItemsModel
import com.ipca.socialstore.data.repository.DeliveryItemsRepository
import com.ipca.socialstore.data.repository.DeliveryRepository
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.delivery.CreateDeliveryUseCase
import com.ipca.socialstore.domain.deliveryItems.CreateDeliveryItemsUseCase
import com.ipca.socialstore.domain.deliveryItems.GetDeliveryItemsBySchedulingIdUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByIdUseCase
import com.ipca.socialstore.domain.stock.WithdrawItemStockByExpirationUseCase
import javax.inject.Inject

class CreateDeliveryServiceUseCase @Inject constructor(
    private val createDeliveryUseCase: CreateDeliveryUseCase,
    private val getDeliveryItemsBySchedulingIdUseCase: GetDeliveryItemsBySchedulingIdUseCase,
    private val deliveryItemsRepository: DeliveryItemsRepository,
    private val deliveryRepository: DeliveryRepository,
    private val withdrawItemStockByExpirationUseCase: WithdrawItemStockByExpirationUseCase,
    private val stockRepository: StockRepository
) {

    suspend fun getItemsByScheduling(schedulingId: Int): ResultWrapper<List<DeliveryItemsModel>> {
        val deliveryResult = deliveryRepository.getDeliveryBySchedulingId(schedulingId)
            return when (deliveryResult) {
                is ResultWrapper.Success -> {
                    val deliveryId = deliveryResult.data.id!!
                    getDeliveryItemsBySchedulingIdUseCase(deliveryId)
                }
                is ResultWrapper.Error -> {
                    ResultWrapper.Error(deliveryResult.error)
                }
            }
        }
    suspend operator fun invoke(schedulingId: Int, stockMap: Map<Int, Int>): ResultWrapper<List<DeliveryItemsModel>> {

        // 1. Criar a entrega (Header)
        val deliveryResult = createDeliveryUseCase(schedulingId)
        if (deliveryResult is ResultWrapper.Error) return ResultWrapper.Error(deliveryResult.error)

        val deliveryId = (deliveryResult as ResultWrapper.Success).data
        val itemsResult = getDeliveryItemsBySchedulingIdUseCase(deliveryId)

        val itemsToInsert = mutableListOf<DeliveryItemsModel>()
        val itemsToUpdate = mutableListOf<DeliveryItemsModel>()
        val itemsToDelete = mutableListOf<Int>()

        // 2. Sincronizar Itens da Entrega
        if (itemsResult is ResultWrapper.Success) {
            val dbItems = itemsResult.data

            stockMap.forEach { (stockId, qty) ->
                val match = dbItems.find { it.stockId == stockId }
                if (match == null) {
                    itemsToInsert.add(DeliveryItemsModel(deliveryId = deliveryId, stockId = stockId, quantity = qty))
                } else if (match.quantity != qty) {
                    itemsToUpdate.add(match.copy(quantity = qty))
                }
            }

            dbItems.forEach { dbItem ->
                if (!stockMap.contains(dbItem.stockId)) {
                    dbItem.id?.let { itemsToDelete.add(it) }
                }
            }
        } else {
            stockMap.forEach { (id, qty) ->
                itemsToInsert.add(DeliveryItemsModel(deliveryId = deliveryId, stockId = id, quantity = qty))
            }
        }

        if (itemsToInsert.isNotEmpty()) deliveryItemsRepository.createDeliveryItems(itemsToInsert)
        if (itemsToUpdate.isNotEmpty()) deliveryItemsRepository.updateDeliveryItems(itemsToUpdate)
        if (itemsToDelete.isNotEmpty()) deliveryItemsRepository.deleteDeliveryItems(itemsToDelete)


        stockMap.forEach { (stockId, qty) ->
            val stockInfo = stockRepository.getStock(stockId)
            if (stockInfo is ResultWrapper.Success) {
                val itemId = stockInfo.data.itemId
                withdrawItemStockByExpirationUseCase(itemId, qty)
            }
        }

        return ResultWrapper.Success(itemsToInsert + itemsToUpdate)
    }
}

