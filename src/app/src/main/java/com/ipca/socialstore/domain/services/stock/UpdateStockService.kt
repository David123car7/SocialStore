package com.ipca.socialstore.domain.services.stock

import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.CampaignRepository
import com.ipca.socialstore.data.repository.ItemRepository
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UpdateStockService @Inject constructor(
    private val itemRepository: ItemRepository,
    private val stockRepository: StockRepository){
    suspend operator fun invoke(item: ItemModel, mapList: List<Map<String, Int>>): ResultWrapper<Unit>{
        val updateItemResult = itemRepository.updateItem(item = item)
        if(updateItemResult is ResultWrapper.Error) return ResultWrapper.Error(updateItemResult.error)

        val allEntries = mapList.flatMap { it.entries }

        for ((expirationDate, quantity) in allEntries) {

            val checkStockResult = stockRepository.getStockByItemInfo(
                id = item.id!!,
                expirationDate = expirationDate
            )

            when (checkStockResult) {
                is ResultWrapper.Success -> {
                    val existingStock = checkStockResult.data
                    val updateResult = stockRepository.updateQuantityInStock(
                        stockId = existingStock.id!!,
                        newQuantity = quantity
                    )
                    if (updateResult is ResultWrapper.Error)
                        return ResultWrapper.Error(updateResult.error)
                }
                is ResultWrapper.Error -> {
                    val insertResult = stockRepository.createStock(
                        stock = StockModel(
                            itemId = item.id,
                            expirationDate = expirationDate,
                            quantity = quantity
                        )
                    )
                    if (insertResult is ResultWrapper.Error)
                        return ResultWrapper.Error(insertResult.error)
                }
            }
        }

        return ResultWrapper.Success(Unit)
    }
}