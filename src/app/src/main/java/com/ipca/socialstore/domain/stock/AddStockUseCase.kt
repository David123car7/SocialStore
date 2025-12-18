package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class AddStockUseCase @Inject constructor(private val stockRepository: StockRepository) {
    suspend operator fun invoke(stock : StockModel) : ResultWrapper<Int>{
        return stockRepository.addStock(stock)
    }
}