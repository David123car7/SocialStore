package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class AddItemStockUseCase @Inject constructor(private val stockRepository: StockRepository){
    suspend operator fun invoke(item : StockModel, quantity : Int) : ResultWrapper<Boolean>{
        return when(val stock = stockRepository.getItemByIdStock(item.itemId!!)){
            is ResultWrapper.Success ->{
                if (stock.data != null){
                    stockRepository.updateStock(item = item,quantity)
                }else{
                    stockRepository.addItemStock(item, quantity)
                }
                ResultWrapper.Success(true)

            }
            is ResultWrapper.Error ->{
                ResultWrapper.Error(stock.error)
            }
        }
    }
}