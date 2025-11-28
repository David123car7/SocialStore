package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSessionStateUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<ResultFlowWrapper<Boolean>> {
        return authRepository.getUserSessionState()
    }
}