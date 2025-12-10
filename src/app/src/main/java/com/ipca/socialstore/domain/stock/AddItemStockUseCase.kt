package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class AddItemStockUseCase @Inject constructor(private val stockRepository: StockRepository){
    suspend operator fun invoke(itemId: Int, expirationDate: String, quantity : Int) : ResultWrapper<Int>{
        return when(val stockResult = stockRepository.getStockByItemInfo(id = itemId, expirationDate = expirationDate)){
            is ResultWrapper.Success ->{
                return stockRepository.updateStock(itemId = itemId, quantity = quantity)
            }
            is ResultWrapper.Error ->{
                if(stockResult.error == AppError.DataNotFound)
                    stockRepository.createStock(StockModel(itemId = itemId, expirationDate = expirationDate, quantity = quantity))
                else
                    return ResultWrapper.Error(stockResult.error)
            }
        }
    }
}