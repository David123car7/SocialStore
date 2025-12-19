package com.ipca.socialstore.domain.services.stock

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.item.GetItemByIdUseCase
import com.ipca.socialstore.domain.stock.CreateItemStockUseCase
import com.ipca.socialstore.presentation.views.item.ExpirationDate
import javax.inject.Inject

class CreateItemStockService @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val createItemStockUseCase: CreateItemStockUseCase,
    private val exceptionMapper: ExceptionMapper
) {
    suspend operator fun invoke(item: ItemModel, list : List<ExpirationDate>) : ResultWrapper<Boolean> {

        return try {
            //Creates Item
            val createItemResult = createItemUseCase(item)
            if(createItemResult is ResultWrapper.Error)
                return ResultWrapper.Error(createItemResult.error)
            val itemId = (createItemResult as ResultWrapper.Success).data

            //Gets Item
            val itemResult = getItemByIdUseCase(itemId = itemId)
            if(itemResult is ResultWrapper.Error)
                return ResultWrapper.Error(itemResult.error)
            val item = (itemResult as ResultWrapper.Success).data

            val stockResult = createItemStockUseCase(itemId = itemId,list)
            if(stockResult is ResultWrapper.Error)
                return ResultWrapper.Error(stockResult.error)
            val stock = (stockResult as ResultWrapper.Success).data

            return ResultWrapper.Success(stock)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}