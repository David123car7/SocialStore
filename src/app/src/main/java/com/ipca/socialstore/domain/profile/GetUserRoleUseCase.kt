package com.ipca.socialstore.domain.profile

import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetUserRoleUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): ResultWrapper<UserRole> {
        return userRepository.getUserRole()
    }
}