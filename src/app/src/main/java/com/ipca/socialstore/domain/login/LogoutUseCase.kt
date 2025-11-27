package com.ipca.socialstore.domain.login

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import java.lang.Exception
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): ResultWrapper<Boolean> {
        return authRepository.logout()
    }
}