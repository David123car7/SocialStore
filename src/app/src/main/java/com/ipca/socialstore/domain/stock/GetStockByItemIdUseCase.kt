package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.views.item.ExpirationDate
import javax.inject.Inject

class GetStockByItemIdUseCase @Inject constructor(
    private val stockRepository: StockRepository
) {
    suspend operator fun invoke(itemId : Int , listExpirationDate : List<String>) : ResultWrapper<List<StockModel>>{
        return stockRepository.getStockByItemId(itemId,listExpirationDate)
    }
}