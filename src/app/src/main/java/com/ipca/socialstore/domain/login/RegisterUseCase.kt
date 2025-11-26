package com.ipca.socialstore.domain.login

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import java.lang.Exception
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): ResultWrapper<Boolean> {
        if(email.isEmpty() || password.isEmpty())
            return ResultWrapper.Error("Not all fields are filled")

        return authRepository.register(email = email, password = password)
    }
}
