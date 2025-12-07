package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class AddItemStockUseCase @Inject constructor(private val stockRepository: StockRepository){
    suspend operator fun invoke(item : StockModel, quantity : Int) : ResultWrapper<StockModel?>{
        return when(val stock = stockRepository.getItemByIdStock(item)){
            is ResultWrapper.Success ->{
                if (stock.data != null){
                    stockRepository.updateStock(item = item,quantity)
                }else{
                    stockRepository.addItemStock(item, quantity)
                }
                ResultWrapper.Success(stock.data)

            }
            is ResultWrapper.Error ->{
                ResultWrapper.Error(stock.error)
            }
        }
    }
}