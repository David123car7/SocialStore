package com.ipca.socialstore.domain.login

import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.data.repository.AuthRepository
import java.lang.Exception
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): ResultWrapper<Boolean> {
        if(email.isEmpty() || password.isEmpty())
            return ResultWrapper.Error("Not all fields are filled")

        return authRepository.login(email = email, password = password)
    }
}