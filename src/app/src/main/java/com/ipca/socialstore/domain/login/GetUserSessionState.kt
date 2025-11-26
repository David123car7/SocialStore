package com.ipca.socialstore.domain.login

import android.app.Activity
import com.ipca.socialstore.data.helpers.ResultWrapper
import com.ipca.socialstore.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSessionState @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<ResultWrapper<Boolean>> {
        return authRepository.getUserSessionState()
    }
}