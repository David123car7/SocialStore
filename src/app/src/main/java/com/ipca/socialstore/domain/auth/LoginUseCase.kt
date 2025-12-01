package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): ResultWrapper<Boolean> {
        if(email.isEmpty())
            return ResultWrapper.Error(AppError.InvalidEmail)

        if(password.isEmpty())
            return ResultWrapper.Error(AppError.InvalidPassword)

        return authRepository.login(email = email, password = password)
    }
}