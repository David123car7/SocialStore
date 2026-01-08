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
    suspend operator fun invoke(item: ItemModel, list : List<ExpirationDate>) : ResultWrapper<Boolean> {

        return try {
            val createItemResult = createItemUseCase(item)
            if(createItemResult is ResultWrapper.Error)
                return ResultWrapper.Error(createItemResult.error)
            val itemId = (createItemResult as ResultWrapper.Success).data

            val datesOnly = list.map { it.date }
            val stockItemResult = getStockByItemIdUseCase(itemId,datesOnly)
            if (stockItemResult is ResultWrapper.Error){
                if (stockItemResult.error == AppError.DataNotFound) emptyList<StockModel>()
                else return ResultWrapper.Error(stockItemResult.error)
            }
            val stockItem = (stockItemResult as ResultWrapper.Success).data

            list.forEach { date ->
                val match = stockItem.find { it.expirationDate == date.date }

                if (match != null){
                    updateQuantityInStockByDateUseCase(match.id!!,date.quantity.toInt())
                }else{
                    val newStock =
                        StockModel(
                            itemId = itemId,
                            expirationDate = date.date,
                            quantity = date.quantity.toInt()
                        )
                    stockRepository.addStock(newStock)
                }
            }
            return ResultWrapper.Success(true)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}