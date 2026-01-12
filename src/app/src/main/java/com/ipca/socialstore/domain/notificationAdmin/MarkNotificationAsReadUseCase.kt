package com.ipca.socialstore.domain.notificationAdmin

import com.ipca.socialstore.data.models.NotificationAdminModel
import com.ipca.socialstore.data.repository.NotificationAdminRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val notificationAdminRepository: NotificationAdminRepository
) {
    suspend operator fun invoke(notificationId: Int) : ResultWrapper<Unit>{
        return notificationAdminRepository.markNotificationAsRead(notificationId = notificationId)
    }
}