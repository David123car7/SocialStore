package com.ipca.socialstore.domain.auth

import android.util.Log
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.objects.ValidEmailNomains
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository, private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String, user: UserModel): ResultWrapper<Boolean> {
        if(email.isEmpty())
            return ResultWrapper.Error(AppError.InvalidEmail)

        if(password.isEmpty())
            return ResultWrapper.Error(AppError.InvalidPassword)

        if(!email.endsWith(ValidEmailNomains.ipcaDomain))
            return ResultWrapper.Error(AppError.InvalidEmailDomain)

        val registerResult = authRepository.register(email = email, password = password)
        if(registerResult is ResultWrapper.Error){
            return ResultWrapper.Error<Boolean>(error = registerResult.error)
        }

        val userWithId = user.copy(uid = registerResult.data, role = UserRole.DEFAULT.value)

        val registerUserResult = userRepository.registerUser(userWithId)
        if(registerUserResult is ResultWrapper.Error){
            return registerUserResult
        }

        return registerUserResult
    }
}