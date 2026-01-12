package com.ipca.socialstore.domain.notificationAdmin

import com.ipca.socialstore.data.repository.NotificationAdminRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class MarkAllNotificationsAsRead @Inject constructor(
    private val notificationAdminRepository: NotificationAdminRepository
) {
    suspend operator fun invoke(notificationIds: List<Int>) : ResultWrapper<Unit>{
        return notificationAdminRepository.markNotificationListAsRead(notificationIds = notificationIds)
    }
}