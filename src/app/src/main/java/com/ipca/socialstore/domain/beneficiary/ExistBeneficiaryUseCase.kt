package com.ipca.socialstore.domain.beneficiary

import com.ipca.socialstore.data.repository.BeneficiaryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class ExistBeneficiaryUseCase @Inject constructor(private val beneficiaryRepository: BeneficiaryRepository) {
    suspend operator fun invoke(id : String) : ResultWrapper<Int>{
        return beneficiaryRepository.existBeneficiary(beneficiaryId = id)
    }
}