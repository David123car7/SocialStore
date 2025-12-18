package com.ipca.socialstore.domain.usecases.user

import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetUserApplicationId @Inject constructor(
    private  val authRepository: AuthRepository,
    private val userRepository: UserRepository) {
    suspend operator fun invoke(): ResultWrapper<Int> {
        val uidResult = authRepository.getUserUid()
        if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)
        val uid = (uidResult as ResultWrapper.Success).data

        return userRepository.getUserApplicationId(uid = uid)
    }
}