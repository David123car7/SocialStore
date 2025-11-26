package com.ipca.socialstore.presentation.home

import android.R
import androidx.compose.runtime.mutableStateOf
import com.ipca.socialstore.domain.login.GetUserSessionState
import com.ipca.socialstore.domain.login.LogoutUseCase
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

class HomeViewModel @Inject constructor(
    private val getUserSessionState: GetUserSessionState,
    private val logoutUseCase: LogoutUseCase) {

    var uiState = mutableStateOf(HomeState())

    fun getUserStateSession(){
        val result = getUserSessionState()
        result.onSuccess { value ->
            uiState.value = uiState.value.copy(isLoading = false, error = null, isLoggedIn = value)
        }
    }

    fun logout(){
        val result = logoutUseCase
    }
}