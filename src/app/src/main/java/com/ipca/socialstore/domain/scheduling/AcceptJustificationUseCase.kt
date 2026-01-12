package com.ipca.socialstore.domain.scheduling

import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class AcceptJustificationUseCase @Inject constructor(
    private val schedulingRepository: SchedulingRepository
) {
    suspend operator fun invoke(schedulingId : Int) : ResultWrapper<SchedulingModel>{
        return schedulingRepository.adminAcceptJustification(schedulingId)
    }
}