package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, token: String): ResultWrapper<Boolean> {
        if(email.isEmpty())
            return ResultWrapper.Error(AppError.InvalidEmail)

        if(password.isEmpty())
            return ResultWrapper.Error(AppError.InvalidPassword)

        if(token.isEmpty())
            return ResultWrapper.Error(AppError.InvalidResetToken)


        val result = authRepository.signInWithToken(email = email, token = token)
        if(result is ResultWrapper.Success)
            return authRepository.resetPassword(password = password)
        else
            return result
    }
}