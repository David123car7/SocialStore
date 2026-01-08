package com.ipca.socialstore.domain.services.stock

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.item.GetItemByIdUseCase
import com.ipca.socialstore.domain.stock.AddItemStockUseCase
import com.ipca.socialstore.domain.stock.CreateItemStockUseCase
import com.ipca.socialstore.domain.stock.GetStockByItemIdUseCase
import com.ipca.socialstore.domain.stock.UpdateQuantityInStockByDateUseCase
import com.ipca.socialstore.presentation.views.item.ExpirationDate
import javax.inject.Inject
import kotlin.collections.emptyList

class CreateItemStockService @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val getStockByItemIdUseCase: GetStockByItemIdUseCase,
    private val updateQuantityInStockByDateUseCase: UpdateQuantityInStockByDateUseCase,
    private val stockRepository: StockRepository,
    private val exceptionMapper: ExceptionMapper
) {
    suspend operator fun invoke(item: ItemModel, list: List<ExpirationDate>): ResultWrapper<Boolean> {
        return try {

            val createItemResult = createItemUseCase(item)
            if (createItemResult is ResultWrapper.Error) return ResultWrapper.Error(createItemResult.error)
            val itemId = (createItemResult as ResultWrapper.Success).data

            val datesForDb = list.map { formatToDbDate(it.date) }
            val stockItemResult = getStockByItemIdUseCase(itemId, datesForDb)

            val stockItem: List<StockModel> = when (stockItemResult) {
                is ResultWrapper.Success -> stockItemResult.data
                is ResultWrapper.Error -> {
                    if (stockItemResult.error == AppError.DataNotFound) emptyList()
                    else return ResultWrapper.Error(stockItemResult.error)
                }
            }
            println("Isto é o item $stockItem")
            list.forEach { entry ->
                println("Entry $entry")
                if (entry.date.isNotBlank() && entry.quantity.isNotBlank()) {

                    val dateInDbFormat = formatToDbDate(entry.date)
                    val match = stockItem.find { it.expirationDate == dateInDbFormat }
                    println(match?.expirationDate)
                    println(dateInDbFormat)

                    if (match != null) {
                        updateQuantityInStockByDateUseCase(match.id!!, entry.quantity.toInt())
                    } else {
                        val newStock = StockModel(
                            itemId = itemId,
                            expirationDate = dateInDbFormat,
                            quantity = entry.quantity.toInt()
                        )
                        stockRepository.addStock(newStock)
                    }
                }
            }
            ResultWrapper.Success(true)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    private fun formatToDbDate(uiDate: String): String {
        return try {
            val normalizedDate = uiDate.replace("/", "-")

            val parts = normalizedDate.split("-")
            if (parts.size == 3) {
                if (parts[0].length == 4) {
                    normalizedDate
                } else {
                    // Se vier como 21-01-2026, inverte
                    "${parts[2]}-${parts[1]}-${parts[0]}"
                }
            } else {
                normalizedDate
            }
        } catch (e: Exception) {
            uiDate
        }
    }
}