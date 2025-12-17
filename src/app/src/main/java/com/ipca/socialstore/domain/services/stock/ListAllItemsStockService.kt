package com.ipca.socialstore.domain.services.stock

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemsFromStockUseCase
import com.ipca.socialstore.domain.stock.GetFullStockUseCase
import com.ipca.socialstore.presentation.models.StockReveiverModel
import javax.inject.Inject
import kotlin.collections.iterator

class ListAllItemsStockService @Inject constructor(
    private val exceptionMapper: ExceptionMapper,
    private val getFullStockUseCase: GetFullStockUseCase,
    private val getItemsFromStockUseCase: GetItemsFromStockUseCase
){
    suspend operator fun invoke() : ResultWrapper<List<StockReveiverModel>> {
        return try {
            val stockResult = getFullStockUseCase()
            if(stockResult is ResultWrapper.Error) return ResultWrapper.Error(stockResult.error)
            val stockList = (stockResult as ResultWrapper.Success).data

            val listItemId = mutableListOf<Int>()
            for (item in stockList){
                listItemId.add(item.itemId)
            }

            val itemsResult = getItemsFromStockUseCase(listItemId)
            if(itemsResult is ResultWrapper.Error) return ResultWrapper.Error(itemsResult.error)
            val itemsList = (itemsResult as ResultWrapper.Success).data

            // Convert List to Map for instant lookup
            val itemsMap = itemsList.associateBy { it.id }

            // Group stock by ItemId
            val groupedStock = stockList.groupBy { it.itemId }

            val stockReveiverList = mutableListOf<StockReveiverModel>()

            // 5. Process each group
            for ((itemId, stocks) in groupedStock) {
                val item = itemsMap[itemId] ?: continue

                val totalQty = stocks.sumOf { it.quantity }

                val dateMap = mutableMapOf<String, Int>()
                for (stock in stocks) {
                    val date = stock.expirationDate
                    val currentQtyForDate = dateMap.getOrDefault(date, 0)
                    dateMap[date] = currentQtyForDate + stock.quantity
                }

                stockReveiverList.add(
                    StockReveiverModel(
                        item = item,
                        totalQuantity = totalQty,
                        stockId = stocks.first().id ?: 0,
                        quantityMap = dateMap
                    )
                )
            }

            ResultWrapper.Success(stockReveiverList)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}