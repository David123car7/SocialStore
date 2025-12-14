package com.ipca.socialstore.domain.notificationScheduling

import com.ipca.socialstore.data.models.NotificationSchedulingModel
import com.ipca.socialstore.data.repository.NotificationSchedulingRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateNotificationUseCase @Inject constructor(private val notificationSchedulingRepository: NotificationSchedulingRepository) {
    suspend operator fun invoke(notification: NotificationSchedulingModel) : ResultWrapper<Int>{
        return notificationSchedulingRepository.createNotificationScheduling(notification)
    }

}