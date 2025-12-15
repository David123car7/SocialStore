package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, newPassword1: String, newPassword2: String,token: String): ResultWrapper<String> {
        if(email.isEmpty())
            return ResultWrapper.Error(AppError.InvalidEmail)

        if(newPassword1.isEmpty() || newPassword2.isEmpty())
            return ResultWrapper.Error(AppError.InvalidPassword)

        if(token.isEmpty())
            return ResultWrapper.Error(AppError.InvalidResetToken)

        if(newPassword1 != newPassword2)
            return ResultWrapper.Error(AppError.DifferentPassword)


        val result = authRepository.signInWithToken(email = email, token = token)
        if(result is ResultWrapper.Error)
            return ResultWrapper.Error(result.error)

        val resetResult = authRepository.resetPassword(password = newPassword1)
        if(resetResult is ResultWrapper.Error){
            authRepository.logout()
            return ResultWrapper.Error(resetResult.error)
        }

        return resetResult
    }
}