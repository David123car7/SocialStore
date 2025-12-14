package com.ipca.socialstore.domain.applicationState

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetUserApplicationState @Inject constructor(
    private val applicationStateRepository: ApplicationStateRepository) {
    suspend operator fun invoke(applicationId: Int): ResultWrapper<ApplicationStateModel> {
        return applicationStateRepository.getApplicationState(id = applicationId)
    }
}