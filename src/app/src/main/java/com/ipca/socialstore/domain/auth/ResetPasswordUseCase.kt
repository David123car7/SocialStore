package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, token: String): ResultWrapper<Boolean> {
        if(email.isEmpty())
            return ResultWrapper.Error("Email is empty")

        if(password.isEmpty())
            return ResultWrapper.Error("Password is empty")

        if(token.isEmpty())
            return ResultWrapper.Error("Token is empty")


        val result = authRepository.signInWithToken(email = email, token = token)
        if(result is ResultWrapper.Success)
            return authRepository.resetPassword(password = password)
        else
            return result
    }
}