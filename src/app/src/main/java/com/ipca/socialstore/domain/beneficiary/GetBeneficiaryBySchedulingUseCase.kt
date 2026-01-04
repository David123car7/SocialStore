package com.ipca.socialstore.domain.beneficiary

import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.repository.BeneficiaryRepository
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetBeneficiaryBySchedulingUseCase @Inject constructor(
    private val schedulingRepository: SchedulingRepository
) {
    suspend operator fun invoke(schedulingId : Int) : ResultWrapper<SchedulingModel>{
        return schedulingRepository.getBeneficiaryBySchedulingId(schedulingId)
    }
}