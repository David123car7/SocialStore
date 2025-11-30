package com.ipca.socialstore.domain.auth

import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper

import javax.inject.Inject

class GetUserRoleUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): ResultWrapper<String> {
        val x = userRepository.getUserRole()
        if(x is ResultWrapper.Error){
            return ResultWrapper.Error(x.message ?: "")
        }

        return x
    }
}