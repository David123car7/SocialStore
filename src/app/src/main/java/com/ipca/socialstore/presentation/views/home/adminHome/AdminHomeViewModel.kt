package com.ipca.socialstore.presentation.views.home.adminHome

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.notifications.GetAdminNotifications
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminHomeState(
    val notifications: List<NotificationReceiverModel> = emptyList(),
    val isLoading: Boolean = false,
    var error : ErrorText? = null,
)

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val getAllAdminNotifications: GetAdminNotifications
): ViewModel()  {
    val uiState = mutableStateOf(AdminHomeState())

    init {
        getNotifications()
    }

    fun getNotifications(){
        viewModelScope.launch {
            val result = getAllAdminNotifications(limit = 3)
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
}