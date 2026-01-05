package com.ipca.socialstore.domain.scheduling

import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class AcceptSchedulingDateUseCase @Inject constructor(private val schedulingRepository: SchedulingRepository) {
    suspend operator fun invoke(id : Int, note : String) : ResultWrapper<SchedulingModel>{
        return schedulingRepository.acceptSchedulingDate(id, note)
    }
}