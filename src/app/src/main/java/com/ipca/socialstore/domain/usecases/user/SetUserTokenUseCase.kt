package com.ipca.socialstore.domain.usecases.user

import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class SetUserTokenUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String, token: String): ResultWrapper<Unit> {
        return userRepository.saveFcmToken(userId = userId, token = token)
    }
}