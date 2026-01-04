package com.ipca.socialstore.domain.services.scheduling

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByBeneficiaryIdUseCase
import com.ipca.socialstore.presentation.models.SchedulingHelperModel
import javax.inject.Inject

class GetAllInfoBeneficiaryUseCase @Inject constructor(
    private val getSchedulingByBeneficiaryIdUseCase: GetSchedulingByBeneficiaryIdUseCase,
    private val exceptionMapper: ExceptionMapper
) {
    suspend operator fun invoke(id : Int) : ResultWrapper<SchedulingHelperModel>{
        return try {
            val getSchedulingResult = getSchedulingByBeneficiaryIdUseCase(id)

            if (getSchedulingResult is ResultWrapper.Error) {
                return ResultWrapper.Error(getSchedulingResult.error)
            }

            val schedulingList = (getSchedulingResult as ResultWrapper.Success).data

            val acceptCount = schedulingList.count { it.state == "accept" }
            val cancelCount = schedulingList.count { it.state == "canceled" }
            ResultWrapper.Success(
                SchedulingHelperModel(
                    scheduling = schedulingList,
                    accept = acceptCount,
                    cancel = cancelCount
                )
            )
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}