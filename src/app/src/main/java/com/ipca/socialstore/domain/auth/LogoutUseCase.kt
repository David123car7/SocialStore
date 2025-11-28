package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): ResultWrapper<Boolean> {
        return authRepository.logout()
    }
}