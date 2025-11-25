package com.ipca.socialstore.domain.login

import com.ipca.socialstore.data.repository.AuthRepository
import java.lang.Exception
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        if(email.isEmpty() || password.isEmpty())
            return Result.failure(Exception("Not all fields are filled"))

        return authRepository.register(email = email, password = password)
    }
}
