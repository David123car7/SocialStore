package com.ipca.socialstore.domain.applicationState

import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UpdateApplicationStateUseCase @Inject constructor(
    private val applicationStateRepository: ApplicationStateRepository) {
    suspend operator fun invoke(applicationState: ApplicationStateModel): ResultWrapper<Int> {
        return applicationStateRepository.updateApplicationState(applicationState = applicationState)
    }
}