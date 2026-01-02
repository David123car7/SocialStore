package com.ipca.socialstore.domain.applicationState

import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class SetUserAppStateMessageUseCase @Inject constructor(
    private val applicationStateRepository: ApplicationStateRepository) {
    suspend operator fun invoke(applicationId: Int, message: String): ResultWrapper<Int> {
        return applicationStateRepository.setApplicationStateMessage(id = applicationId, message = message)
    }
}