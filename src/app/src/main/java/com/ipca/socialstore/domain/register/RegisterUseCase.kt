package com.ipca.socialstore.domain.register

import com.ipca.socialstore.data.objects.ValidEmailNomains
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): ResultWrapper<Boolean> {
        if(email.isEmpty() || password.isEmpty())
            return ResultWrapper.Error("Not all fields are filled")

        if(!email.endsWith(ValidEmailNomains.ipcaDomain))
            return ResultWrapper.Error("Email does not belong to IPCA")

        return authRepository.register(email = email, password = password)
    }
}