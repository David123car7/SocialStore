package com.ipca.socialstore.domain.services.scheduling

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.scheduling.GetSchedulingByBeneficiaryIdUseCase
import com.ipca.socialstore.domain.usecases.user.GetUserBeneficiaryIdUseCase
import com.ipca.socialstore.presentation.models.SchedulingHelperModel
import javax.inject.Inject

class GetAllSchedulingUserUseCase @Inject constructor(
    private val getUserBeneficiaryIdUseCase: GetUserBeneficiaryIdUseCase,
    private val getSchedulingByBeneficiaryIdUseCase: GetSchedulingByBeneficiaryIdUseCase,
    private val exceptionMapper: ExceptionMapper
){

    suspend operator fun invoke() : ResultWrapper<SchedulingHelperModel>{
        return try {
            val beneficiaryResult = getUserBeneficiaryIdUseCase()
            if (beneficiaryResult is ResultWrapper.Error) return ResultWrapper.Error(beneficiaryResult.error)
            val beneficiaryId = (beneficiaryResult as ResultWrapper.Success).data

            val schedulingResult = getSchedulingByBeneficiaryIdUseCase(beneficiaryId)
            if (schedulingResult is ResultWrapper.Error) return ResultWrapper.Error(schedulingResult.error)
            val scheduling = (schedulingResult as ResultWrapper.Success).data

            val acceptCount = scheduling.count { it.state == "accept" }
            val cancelCount = scheduling.count { it.state == "canceled" }

            ResultWrapper.Success(
                SchedulingHelperModel(
                    scheduling = scheduling,
                    accept = acceptCount,
                    cancel = cancelCount
                )
            )
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

}
