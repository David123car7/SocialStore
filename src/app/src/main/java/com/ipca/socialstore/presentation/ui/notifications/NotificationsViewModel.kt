package com.ipca.socialstore.presentation.ui.notifications

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.notificationAdmin.MarkAllNotificationsAsRead
import com.ipca.socialstore.domain.notificationAdmin.MarkNotificationAsReadUseCase
import com.ipca.socialstore.domain.services.notifications.GetNotificationsByUser
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsState(
    val notifications: List<NotificationReceiverModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: ErrorText? = null,
    val userRole: UserRole? = null,
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsByUser: GetNotificationsByUser,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsRead,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase
): ViewModel() {
    val uiState = mutableStateOf(NotificationsState())

    fun markNotificationAsRead(id: Int){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = markNotificationAsReadUseCase(notificationId = id)
            when (result) {
                is ResultWrapper.Success -> {
                    val updatedList = uiState.value.notifications.filter { it.id != id }
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        notifications = updatedList
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }

    fun markAllNotificationsAsRead(){
        viewModelScope.launch {
            val ids = uiState.value.notifications.mapNotNull { it.id }
            uiState.value = uiState.value.copy(isLoading = true)
            val result = markAllNotificationsAsReadUseCase(notificationIds = ids)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        notifications = emptyList()
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText()
                    )

                }
            }
        }
    }

    suspend fun getNotifications(userRole: UserRole){
        uiState.value = uiState.value.copy(isLoading = true)
        val result = getNotificationsByUser(userRole = userRole)
        when(result){
            is ResultWrapper.Success -> {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    notifications = result.data
                )
            }
            is ResultWrapper.Error -> {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = result.error.asUiText()
                )

            }
        }
    }
}