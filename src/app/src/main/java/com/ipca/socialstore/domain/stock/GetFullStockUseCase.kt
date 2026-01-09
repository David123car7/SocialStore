package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFullStockUseCase @Inject constructor(private val stockRepository: StockRepository){
    operator fun invoke(): Flow<ResultWrapper<List<StockModel>>> {
        return stockRepository.getFullStockFlow()
    }
}