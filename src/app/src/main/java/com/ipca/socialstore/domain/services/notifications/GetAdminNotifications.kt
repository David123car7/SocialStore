package com.ipca.socialstore.domain.services.notifications

import com.ipca.socialstore.data.repository.NotificationAdminRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import javax.inject.Inject

class  GetAdminNotifications @Inject constructor(
    private val notificationsAdminRepository: NotificationAdminRepository,
){
    suspend operator fun invoke(limit: Int): ResultWrapper<List<NotificationReceiverModel>>{
        return notificationsAdminRepository.getAllNotifications(limit = limit)
    }
}