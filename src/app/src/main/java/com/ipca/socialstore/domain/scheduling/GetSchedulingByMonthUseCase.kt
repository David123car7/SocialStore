package com.ipca.socialstore.domain.scheduling

import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSchedulingByMonthUseCase @Inject constructor(private val schedulingRepository: SchedulingRepository) {
    suspend operator fun invoke(month : Int, year: Int): ResultWrapper<List<SchedulingModel>> {
        return schedulingRepository.getSchedulingByMonth(month,year)
    }
}