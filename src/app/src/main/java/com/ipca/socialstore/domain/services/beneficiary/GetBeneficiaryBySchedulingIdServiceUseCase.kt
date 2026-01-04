package com.ipca.socialstore.domain.services.beneficiary

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryBySchedulingUseCase
import javax.inject.Inject

class GetBeneficiaryBySchedulingIdServiceUseCase @Inject constructor(
    private val getBeneficiaryByIdUseCase: GetBeneficiaryByIdUseCase,
    private val getBeneficiaryBySchedulingUseCase: GetBeneficiaryBySchedulingUseCase,
){
    suspend operator fun invoke(schedulingId:Int): ResultWrapper<BeneficiaryModel>{
        val schedulingResult = getBeneficiaryBySchedulingUseCase(schedulingId)
        if (schedulingResult is ResultWrapper.Error) return ResultWrapper.Error(schedulingResult.error)
        val schedulingId = (schedulingResult as ResultWrapper.Success).data
        println(schedulingResult.data)
        val id = schedulingId.beneficiaryId
        val beneficiaryResult = getBeneficiaryByIdUseCase(id)
        if (beneficiaryResult is ResultWrapper.Error)
            return ResultWrapper.Error(beneficiaryResult.error)
        val beneficiary = (beneficiaryResult as ResultWrapper.Success).data
        return ResultWrapper.Success(beneficiary)
    }

}