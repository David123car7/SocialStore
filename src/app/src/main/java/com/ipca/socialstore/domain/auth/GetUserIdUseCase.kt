package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): ResultWrapper<String> {
        return authRepository.getUserUid()
    }
}