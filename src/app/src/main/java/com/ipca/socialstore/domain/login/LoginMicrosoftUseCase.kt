package com.ipca.socialstore.domain.login

import android.app.Activity
import com.ipca.socialstore.data.repository.AuthRepository
import java.lang.Exception
import javax.inject.Inject

class LoginMicrosoftUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(activity: Activity): Result<Boolean> {
        return authRepository.loginWithMicrosoft(activity = activity)
    }
}