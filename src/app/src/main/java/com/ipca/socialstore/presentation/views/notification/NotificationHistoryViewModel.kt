package com.ipca.socialstore.presentation.views.notification

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.notifications.GetAllNotificationsByUser
import com.ipca.socialstore.domain.services.notifications.GetNotificationsByUser
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



data class NotificationState(
    val notifications : List<NotificationReceiverModel> = emptyList(),
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
)
@HiltViewModel
class NotificationViewModel  @Inject constructor(
    private val getAllNotificationsByUser: GetAllNotificationsByUser,
    ) : ViewModel() {
    val uiState = mutableStateOf(NotificationState())

    suspend fun getNotifications(userRole: UserRole){
        uiState.value = uiState.value.copy(isLoading = true)
        val result = getAllNotificationsByUser(userRole = userRole)
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

