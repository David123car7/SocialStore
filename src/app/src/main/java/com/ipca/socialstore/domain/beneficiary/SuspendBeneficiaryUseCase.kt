package com.ipca.socialstore.domain.beneficiary

import com.ipca.socialstore.data.repository.BeneficiaryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class SuspendBeneficiaryUseCase @Inject constructor(
    private val beneficiaryRepository: BeneficiaryRepository
) {
    suspend operator fun invoke(beneficiaryId : Int) : ResultWrapper<Boolean>{
        return beneficiaryRepository.suspendBeneficiary(beneficiaryId)
    }
}
