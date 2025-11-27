package com.ipca.socialstore.domain.login

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): ResultWrapper<Boolean> {
        if(email.isEmpty())
            return ResultWrapper.Error("Email is empty")

        return authRepository.resetPassword(email)
    }
}