package com.ipca.socialstore.presentation.authentication.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.login.LoginUseCase
import com.ipca.socialstore.domain.login.ResetPasswordUseCase
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
    private val loginUseCase: LoginUseCase): ViewModel() {
    var uiState = mutableStateOf(LoginState())

    fun updateEmail(email : String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun updatePassword(password : String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun login(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = loginUseCase(uiState.value.email, uiState.value.password)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = result.message
                    )
                }
            }
        }
    }
}