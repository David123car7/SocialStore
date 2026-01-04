package com.ipca.socialstore.domain.services.scheduling

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetListBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByMonthUseCase
import com.ipca.socialstore.presentation.models.SchedulingReceiverModel
import javax.inject.Inject

class GetSchedulingInfoByMonthUseCase @Inject constructor(
    private val getSchedulingByMonthUseCase: GetSchedulingByMonthUseCase,
    private val getListBeneficiaryByIdUseCase: GetListBeneficiaryByIdUseCase,
    private val exceptionMapper: ExceptionMapper
) {
    suspend operator fun invoke(month : Int, year: Int) : ResultWrapper<List<SchedulingReceiverModel>>{

        return try {
            val getSchedulingResult = getSchedulingByMonthUseCase(month, year)
            if (getSchedulingResult is ResultWrapper.Error)
                return ResultWrapper.Error(getSchedulingResult.error)
            val scheduling = (getSchedulingResult as ResultWrapper.Success).data

            val ids = scheduling.map { it.beneficiaryId }

            val getBeneficiariesResult = getListBeneficiaryByIdUseCase(ids)
            if (getBeneficiariesResult is ResultWrapper.Error)
                return ResultWrapper.Error(getBeneficiariesResult.error)
            val beneficiaryList = (getBeneficiariesResult as ResultWrapper.Success).data

            val finalResult = scheduling.map { schedule ->
                val benef = beneficiaryList.find { it.id == schedule.beneficiaryId }

                SchedulingReceiverModel(
                    name = benef?.name,
                    date = schedule.schedulingDate,
                    schedulingId = schedule.id!!
                )
            }
            ResultWrapper.Success(finalResult)
        }catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}