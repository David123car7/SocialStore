package com.ipca.socialstore.domain.notificationAdmin

import com.ipca.socialstore.data.models.NotificationAdminModel
import com.ipca.socialstore.data.repository.NotificationAdminRepository
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CreateAdminNotificationUseCase @Inject constructor(
    private val notificationAdminRepository: NotificationAdminRepository
) {
    suspend operator fun invoke(tittle: String, description: String, type: String) : ResultWrapper<Int>{
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val currentDate = LocalDate.now().format(formatter)
        val notification = NotificationAdminModel(
            tittle = tittle,
            description = description,
            read = false,
            type = type,
            created_at = currentDate
        )
        return notificationAdminRepository.createNotification(notification = notification)
    }
}