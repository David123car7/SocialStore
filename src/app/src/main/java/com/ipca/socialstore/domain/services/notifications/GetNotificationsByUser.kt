package com.ipca.socialstore.domain.services.notifications

import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.CampaignRepository
import com.ipca.socialstore.data.repository.NotificationAdminRepository
import com.ipca.socialstore.data.repository.NotificationBeneficiaryRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import javax.inject.Inject

class  GetNotificationsByUser @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationsAdminRepository: NotificationAdminRepository,
    private val notificationsBeneficiaryRepository: NotificationBeneficiaryRepository
){
    suspend operator fun invoke(userRole: UserRole): ResultWrapper<List<NotificationReceiverModel>>{
        var notifications: List<NotificationReceiverModel> = emptyList()
        if(userRole == UserRole.ADMIN){
            val notificationsResult = notificationsAdminRepository.getNotificationsUnread()
            if(notificationsResult is ResultWrapper.Error)
                return ResultWrapper.Error(notificationsResult.error)
            notifications = (notificationsResult as ResultWrapper.Success).data
        }
        else{
            val uidResult = authRepository.getUserUid()
            if(uidResult is ResultWrapper.Error)
                return ResultWrapper.Error(uidResult.error)
            val uid = (uidResult as ResultWrapper.Success).data

            val notificationsResult = notificationsBeneficiaryRepository.getNotificationsUnread(uid = uid)
            if(notificationsResult is ResultWrapper.Error)
                return ResultWrapper.Error(notificationsResult.error)
            notifications = (notificationsResult as ResultWrapper.Success).data
        }

        return ResultWrapper.Success(notifications)
    }
}