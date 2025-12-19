package com.ipca.socialstore.domain.notificationSchedule

import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.repository.NotificationRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetAllNotificationsUseCase @Inject constructor(private val notificationRepository: NotificationRepository){
    suspend operator fun invoke() : ResultWrapper<List<NotificationScheduledModel>>{
        return notificationRepository.getAllNotifications()
    }
}