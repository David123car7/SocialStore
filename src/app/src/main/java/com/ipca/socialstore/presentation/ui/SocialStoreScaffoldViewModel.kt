package com.ipca.socialstore.presentation.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.LogoutUseCase
import com.ipca.socialstore.domain.services.notifications.GetNotificationsByUser
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.views.home.defaultHomeView.DefaultHomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SocialStoreScaffoldState (
    var error : ErrorText? = null,
    var isLoading : Boolean = false,
)

@HiltViewModel
class SocialStoreScaffoldViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase): ViewModel() {
    var uiState = mutableStateOf(DefaultHomeState())

    fun logout(){
        viewModelScope.launch {
            val result = logoutUseCase()
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
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