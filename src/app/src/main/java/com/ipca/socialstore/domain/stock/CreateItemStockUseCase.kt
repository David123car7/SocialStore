package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.views.item.ExpirationDate
import javax.inject.Inject

class CreateItemStockUseCase @Inject constructor(private val stockRepository: StockRepository) {
    suspend operator fun invoke(itemId: Int, list : List<ExpirationDate>) : ResultWrapper<Boolean>{
        return stockRepository.createItemStock(itemId, list)
    }
}