package com.ipca.socialstore.domain.stock

import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class WithdrawItemStockByExpirationUseCase @Inject constructor(
    private val stockRepository: StockRepository,
) {
    suspend operator fun invoke(itemId : Int, qty : Int): ResultWrapper<Boolean>{
        return stockRepository.withdrawItemStockByExpiration(itemId,qty)
    }
}