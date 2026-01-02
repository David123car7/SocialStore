package com.ipca.socialstore.domain.application

import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.repository.ApplicationDataStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UpdateApplicationDataStateUseCase @Inject constructor(
    private val applicationDataStateRepository: ApplicationDataStateRepository) {
    suspend operator fun invoke(applicationDataStateModel: ApplicationDataStateModel): ResultWrapper<Int> {
        return applicationDataStateRepository.updateApplicationDataState(applicationDataState = applicationDataStateModel)
    }
}