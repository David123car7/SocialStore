package com.ipca.socialstore.domain.application

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetUserApplicationUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private  val authRepository: AuthRepository,
    private val userRepository: UserRepository) {
    suspend operator fun invoke(): ResultWrapper<ApplicationModel> {
        val uidResult = authRepository.getUserUid()
        if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)
        val uid = (uidResult as ResultWrapper.Success).data

        val applicationIdResult = userRepository.getUserApplicationId(uid = uid)
        if (applicationIdResult is ResultWrapper.Error) return ResultWrapper.Error(applicationIdResult.error)
        val applicationId = (applicationIdResult as ResultWrapper.Success).data

        return applicationRepository.getApplication(id = applicationId)
    }
}