package com.ipca.socialstore.domain.notificationUser

import com.ipca.socialstore.data.models.NotificationAdminModel
import com.ipca.socialstore.data.models.NotificationUserModel
import com.ipca.socialstore.data.repository.NotificationAdminRepository
import com.ipca.socialstore.data.repository.NotificationBeneficiaryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CreateUserNotificationUseCase @Inject constructor(
    private val notificationBeneficiaryRepository: NotificationBeneficiaryRepository,
) {
    suspend operator fun invoke(userId: String,tittle: String, description: String, type: String) : ResultWrapper<Int>{
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val currentDate = LocalDate.now().format(formatter)
        val notification = NotificationUserModel(
            tittle = tittle,
            description = description,
            read = false,
            type = type,
            created_at = currentDate,
            userId = userId
        )
        return notificationBeneficiaryRepository.createNotification(notification = notification)
    }
}