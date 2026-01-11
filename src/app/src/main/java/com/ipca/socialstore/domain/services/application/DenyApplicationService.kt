package com.ipca.socialstore.domain.services.application

import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class DenyApplicationService @Inject constructor(private val applicationStateRepository: ApplicationStateRepository){
    suspend operator fun invoke(appStateId: Int): ResultWrapper<Unit>{
        val appStateResult = applicationStateRepository.updateApplicationState(
            applicationState = ApplicationStateModel(
                id = appStateId, state =
                    ApplicationStates.REJECTED.status
            )
        )
        if(appStateResult is ResultWrapper.Error)
            return ResultWrapper.Error(appStateResult.error)

        return ResultWrapper.Success(Unit)
    }
}