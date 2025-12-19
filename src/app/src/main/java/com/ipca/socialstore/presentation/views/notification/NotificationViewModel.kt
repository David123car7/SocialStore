package com.ipca.socialstore.presentation.views.notification

import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.Work.ExpirationDateWorker
import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.notificationSchedule.GetAllNotificationsUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



data class NotificationState(
    val notification : List<NotificationScheduledModel> ?= null,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
)
@HiltViewModel
class NotificationViewModel  @Inject constructor(
    private val getAllNotificationsUseCase: GetAllNotificationsUseCase
) : ViewModel() {



    val uiState = mutableStateOf(NotificationState())


    fun getAllNotification() {
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )
        viewModelScope.launch {
            val result = getAllNotificationsUseCase()
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        notification = result.data,
                        isLoading = false,
                        error = null
                    )
                    println(uiState.value.notification)
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

}

