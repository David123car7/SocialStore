package com.ipca.socialstore.domain.usecases.user

import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class IsUserAdminUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): ResultWrapper<Boolean> {
        val roleResult = userRepository.getUserRole()
        if(roleResult is ResultWrapper.Error) return ResultWrapper.Error(roleResult.error)
        val role = (roleResult as ResultWrapper.Success).data

        if(role.value != UserRole.ADMIN.value)
            return ResultWrapper.Success(false)
        else
            return ResultWrapper.Success(true)

    }
}