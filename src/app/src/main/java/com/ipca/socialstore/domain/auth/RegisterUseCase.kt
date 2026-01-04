package com.ipca.socialstore.domain.auth

import android.content.ContentValues.TAG
import android.util.Log
import com.ipca.socialstore.R
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.ProfileModel
import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.objects.ValidEmailNomains
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.ProfileRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(email: String, password: String): ResultWrapper<Int> {
        if(email.isEmpty())
            return ResultWrapper.Error(AppError.InvalidEmail)

        if(password.isEmpty())
            return ResultWrapper.Error(AppError.InvalidPassword)

        if(!email.endsWith(ValidEmailNomains.ipcaDomain))
            return ResultWrapper.Error(AppError.InvalidEmailDomain)

        val registerResult = authRepository.register(email = email, password = password)
        if(registerResult is ResultWrapper.Error) return ResultWrapper.Error(error = registerResult.error)
        val newUserId = (registerResult as ResultWrapper.Success).data

        val user = UserModel(id = newUserId, role = UserRole.DEFAULT.value, profileId = null, applicationId = null,beneficiaryId = null)
        return userRepository.createUser(user)
    }
}