package com.ipca.socialstore.domain.services

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemsFromStockUseCase
import com.ipca.socialstore.domain.stock.GetFullStockUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.iterator

class ListAllItemsStockService @Inject constructor(
    private val exceptionMapper: ExceptionMapper,
    private val getFullStockUseCase: GetFullStockUseCase,
    private val getItemsFromStockUseCase: GetItemsFromStockUseCase
) {
    operator fun invoke(): Flow<ResultWrapper<List<StockReceiverModel>>> {
        return getFullStockUseCase()
            .map { stockResult ->
                when (stockResult) {
                    is ResultWrapper.Error -> {
                        ResultWrapper.Error(stockResult.error)
                    }

                    is ResultWrapper.Success -> {
                        val stockList = stockResult.data

                        val listItemId = stockList.map { it.itemId }

                        val itemsResult = getItemsFromStockUseCase(listItemId)
                        if (itemsResult is ResultWrapper.Error) {
                            return@map ResultWrapper.Error(itemsResult.error)
                        }

                        val itemsList =
                            (itemsResult as ResultWrapper.Success).data

                        val itemsMap = itemsList.associateBy { it.id }
                        val groupedStock = stockList.groupBy { it.itemId }

                        val stockReceiverList = groupedStock.mapNotNull { (itemId, stocks) ->
                            val item = itemsMap[itemId] ?: return@mapNotNull null

                            val totalQty = stocks.sumOf { it.quantity }

                            val dateMap = stocks
                                .groupBy { it.expirationDate }
                                .mapValues { entry ->
                                    entry.value.sumOf { it.quantity }
                                }

                            StockReceiverModel(
                                item = item,
                                totalQuantity = totalQty,
                                stockId = stocks.first().id ?: 0,
                                quantityMap = dateMap
                            )
                        }

                        ResultWrapper.Success(stockReceiverList)
                    }
                }
            }
            .catch { e ->
                emit(ResultWrapper.Error(exceptionMapper.map(e)))
            }
    }
}

