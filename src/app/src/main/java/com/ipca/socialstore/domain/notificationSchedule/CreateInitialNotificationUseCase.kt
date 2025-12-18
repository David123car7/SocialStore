package com.ipca.socialstore.domain.notificationSchedule

import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.repository.NotificationRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateInitialNotificationUseCase @Inject constructor(private val notificationRepository: NotificationRepository) {
    suspend operator fun invoke(notification : NotificationScheduledModel) : ResultWrapper<Boolean>{
        return notificationRepository.createInitialNotification(notification)
    }
}