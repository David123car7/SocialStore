package com.ipca.socialstore.domain.services.application

import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.repository.AcademicRepository
import com.ipca.socialstore.data.repository.ApplicationDataStateRepository
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UpdateApplicationService @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val applicationDataStateRepository: ApplicationDataStateRepository,
    private val academicRepository: AcademicRepository,
){
    suspend operator fun invoke(
        application: ApplicationModel,
        academicModel: AcademicModel?,
        applicationDataStateId: Int): ResultWrapper<Unit> {
        val updateAppResult = applicationRepository.updateApplication(application = application)
        if(updateAppResult is ResultWrapper.Error) return ResultWrapper.Error(updateAppResult.error)
        if(academicModel != null){
            val updateAcademicDataResult = academicRepository.updateAcademicData(academicModel = academicModel)
            if(updateAcademicDataResult is ResultWrapper.Error) return ResultWrapper.Error(updateAcademicDataResult.error)
        }

        val updateApplicationDataState = applicationDataStateRepository.updateApplicationDataState(
            applicationDataState = ApplicationDataStateModel(
                id = applicationDataStateId,
                state = ApplicationDataStatus.TO_REVIEW.status,
                message = ""
            )
        )
        if(updateApplicationDataState is ResultWrapper.Error) return ResultWrapper.Error(updateApplicationDataState.error)

        return ResultWrapper.Success(Unit)
    }
}