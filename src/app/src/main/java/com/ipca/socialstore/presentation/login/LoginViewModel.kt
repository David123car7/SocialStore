package com.ipca.socialstore.presentation.login

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ipca.socialstore.domain.login.GetUserSessionState
import com.ipca.socialstore.domain.login.LoginMicrosoftUseCase
import com.ipca.socialstore.domain.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState (
    var email : String = "",
    var password : String = "",
    var error : String? = null,
    var isLoading : Boolean = false,
    var isLoggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginMicrosoftUseCase: LoginMicrosoftUseCase,
    private val getUserSessionState: GetUserSessionState): ViewModel() {
    var uiState = mutableStateOf(LoginState())

    fun updateEmail(email : String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun updatePassword(password : String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun getUserStateSession(){
        val result = getUserSessionState()
        result.onSuccess { value ->
            uiState.value = uiState.value.copy(isLoading = false, error = null, isLoggedIn = value)
        }
    }

    fun login(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = loginUseCase(uiState.value.email, uiState.value.password)
            result.onSuccess {
                uiState.value = uiState.value.copy(isLoading = false, error = null, isLoggedIn = true)
            }
            result.onFailure { exception ->
                uiState.value = uiState.value.copy(isLoading = false, error = exception.message)
            }
        }
    }

    fun loginMicrosoft(activity: Activity){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = loginMicrosoftUseCase(activity = activity)
            result.onSuccess {
                uiState.value = uiState.value.copy(isLoading = false, error = null, isLoggedIn = true)
            }
            result.onFailure { exception ->
                uiState.value = uiState.value.copy(isLoading = false, error = exception.message)
            }
        }
    }
}