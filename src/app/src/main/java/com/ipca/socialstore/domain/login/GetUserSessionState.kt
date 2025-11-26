package com.ipca.socialstore.domain.login

import android.app.Activity
import com.ipca.socialstore.data.repository.AuthRepository
import javax.inject.Inject

class GetUserSessionState @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Result<Boolean> {
        return authRepository.getUserSessionState()
    }
}