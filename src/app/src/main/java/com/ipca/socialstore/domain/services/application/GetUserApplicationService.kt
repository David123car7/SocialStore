package com.ipca.socialstore.domain.services.application

import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.repository.AcademicRepository
import com.ipca.socialstore.data.repository.ApplicationDataStateRepository
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.utils.getRequestTypeDisplayLabel
import javax.inject.Inject

class GetUserApplicationService @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val applicationRepository: ApplicationRepository,
    private val applicationStateRepository: ApplicationStateRepository,
    private val applicationDataStateRepository: ApplicationDataStateRepository,
    private val academicRepository: AcademicRepository
) {
    suspend operator fun invoke(appId: Int? = null): ResultWrapper<ApplicationModelReceiver> {
        var applicationId = appId
        if(applicationId == null){
            val uidResult = authRepository.getUserUid()
            if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)
            val uid = (uidResult as ResultWrapper.Success).data

            val applicationIdResult = userRepository.getUserApplicationId(uid = uid)
            if (applicationIdResult is ResultWrapper.Error) return ResultWrapper.Error(applicationIdResult.error)
            applicationId = (applicationIdResult as ResultWrapper.Success).data
        }

        val applicationResult = applicationRepository.getApplication(applicationId)
        if (applicationResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = applicationResult.error)
        val application = (applicationResult as ResultWrapper.Success).data

        val applicationStateResult =
            applicationStateRepository.getApplicationState(id = application.stateId)
        if (applicationStateResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = applicationStateResult.error)
        val state = (applicationStateResult as ResultWrapper.Success).data

        val appDataStateResult =
            applicationDataStateRepository.getApplicationState(id = application.dataStateId)
        if (appDataStateResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = appDataStateResult.error)
        val dataState = (appDataStateResult as ResultWrapper.Success).data

        var academicData: AcademicModel? = null
        if (application.academicId != null) {
            val academicDataResult = academicRepository.getAcademic(id = application.academicId)
            if (academicDataResult is ResultWrapper.Error)
                return ResultWrapper.Error(error = academicDataResult.error)
            academicData = (academicDataResult as ResultWrapper.Success).data
        }

        return ResultWrapper.Success(
            ApplicationModelReceiver(
                id = applicationId,
                schoolYear = application.schoolYear,
                name = application.name,
                email = application.email,
                phoneNumber = application.phoneNumber,
                cc = application.cc,
                createdAt = application.createdAt,
                birthDate = application.birthDate,
                requestType = getRequestTypeDisplayLabel(application.requestType),
                applicationState = state,
                applicationDataState = dataState,
                academicData = academicData
            )
        )
    }
}