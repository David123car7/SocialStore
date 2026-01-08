package com.ipca.socialstore.domain.stock

import androidx.annotation.IntRange
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UpdateQuantityInStockByDateUseCase @Inject constructor(private val stockRepository: StockRepository) {
    suspend operator fun invoke(itemId : Int, quantity : Int) : ResultWrapper<Int>{
        return stockRepository.updateQuantityInStockByDate(itemId,quantity)
    }
}