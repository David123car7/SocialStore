package com.ipca.socialstore.domain.services.beneficiary

import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.usecases.user.GetUserBeneficiaryIdUseCase
import javax.inject.Inject

class GetBeneficiaryByUidUseCase @Inject constructor(
    private val getUserBeneficiaryIdUseCase: GetUserBeneficiaryIdUseCase,
    private val getBeneficiaryByIdUseCase: GetBeneficiaryByIdUseCase
) {
    suspend operator fun invoke() : ResultWrapper<BeneficiaryModel>{
        val beneficiaryResult = getUserBeneficiaryIdUseCase()
        if (beneficiaryResult is ResultWrapper.Error) return ResultWrapper.Error(beneficiaryResult.error)
        val beneficiaryId = (beneficiaryResult as ResultWrapper.Success).data

        val getBeneficiary = getBeneficiaryByIdUseCase(beneficiaryId)
        if (getBeneficiary is ResultWrapper.Error) return ResultWrapper.Error(getBeneficiary.error)
        val beneficiary = (getBeneficiary as ResultWrapper.Success).data

        return ResultWrapper.Success(beneficiary)
    }
}