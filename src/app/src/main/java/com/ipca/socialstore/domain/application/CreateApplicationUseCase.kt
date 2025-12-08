package com.ipca.socialstore.domain.application

import com.ipca.socialstore.R
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.utils.asUiText
import javax.inject.Inject

class CreateApplicationUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val applicationStateRepository: ApplicationStateRepository) {
    suspend operator fun invoke(applicationModel: ApplicationModel): ResultWrapper<Boolean> {
        if(applicationModel.name.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.name))

        if(applicationModel.birthDate.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.date))

        if(applicationModel.cc.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.cc))

        if(applicationModel.phoneNumber.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.phone_number))

        if(applicationModel.schoolYear == 0)
            return ResultWrapper.Error(AppError.InvalidField(R.string.schoolYear))

        if(applicationModel.requestType.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.request_type))

        val x = applicationStateRepository.createApplicationState(applicationState = ApplicationStateModel(state = ApplicationStatus.PENDING.value))
        if(x is ResultWrapper.Error)
            return ResultWrapper.Error(x.error);

        if(x.data == null){
            return ResultWrapper.Error(AppError.UnknownError("Error Creating Application State"));
        }

        return applicationRepository.createApplication(applicationModel.copy(stateId = x.data))
    }
}