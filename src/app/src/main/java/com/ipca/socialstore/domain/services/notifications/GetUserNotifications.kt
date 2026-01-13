package com.ipca.socialstore.domain.services.notifications

import com.ipca.socialstore.data.repository.NotificationBeneficiaryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import javax.inject.Inject

class  GetUserNotifications @Inject constructor(
    private val notificationBeneficiaryRepository: NotificationBeneficiaryRepository
){
    suspend operator fun invoke(limit: Int): ResultWrapper<List<NotificationReceiverModel>>{
        return notificationBeneficiaryRepository.getAllNotifications(limit = limit)
    }
}