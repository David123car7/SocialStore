package com.ipca.socialstore.domain.item

import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetItemNameUseCase @Inject constructor(private val stockRepository: StockRepository) {

}