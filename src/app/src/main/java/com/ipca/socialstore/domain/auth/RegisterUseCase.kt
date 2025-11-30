package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.objects.ValidEmailNomains
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository, private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String, user: UserModel): ResultWrapper<Boolean> {
        if(email.isEmpty() || password.isEmpty())
            return ResultWrapper.Error("Not all fields are filled")

        if(!email.endsWith(ValidEmailNomains.ipcaDomain))
            return ResultWrapper.Error("Email does not belong to IPCA")

        val registerResult = authRepository.register(email = email, password = password)
        if(registerResult is ResultWrapper.Error){
            return ResultWrapper.Error<Boolean>(
                message = registerResult.message ?: "Registration Failed"
            )
        }

        val userWithId = user.copy(uid = registerResult.data)

        val registerUserResult = userRepository.registerUser(userWithId)
        if(registerUserResult is ResultWrapper.Error){
            return registerUserResult
        }

        return registerUserResult
    }
}