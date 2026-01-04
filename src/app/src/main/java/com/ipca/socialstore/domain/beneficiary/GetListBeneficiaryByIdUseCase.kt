package com.ipca.socialstore.domain.beneficiary

import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.repository.BeneficiaryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetListBeneficiaryByIdUseCase @Inject constructor(private val beneficiaryRepository: BeneficiaryRepository) {
    suspend operator fun invoke(id : List<Int>) : ResultWrapper<List<BeneficiaryModel>>{
        return beneficiaryRepository.getListBeneficiaryById(id)
    }
}