package com.ipca.socialstore.domain.notification

import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class WorkerExpirationDateUseCase @Inject constructor(private val stockRepository: StockRepository) {
    suspend operator fun invoke() : ResultWrapper<List<Int>>{
        return stockRepository.workerExpirationDate()
    }
}