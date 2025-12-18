package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class RemoveStockUseCase @Inject constructor(private val stockRepository: StockRepository) {
    suspend operator fun invoke(stockId: Int) : ResultWrapper<Boolean>{
        return stockRepository.removeStock(stockId)
    }
}