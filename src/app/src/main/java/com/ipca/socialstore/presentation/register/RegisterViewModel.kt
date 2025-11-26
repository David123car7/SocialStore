package com.ipca.socialstore.presentation.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.domain.login.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState (
    var email : String = "",
    var password : String = "",
    var error : String? = null,
    var isLoading : Boolean = false,
    var isRegistered : Boolean = false,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(private val registerUseCase: RegisterUseCase): ViewModel() {
    var uiState = mutableStateOf(RegisterState())

    fun updateEmail(email : String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun updatePassword(password : String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun register(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = registerUseCase(uiState.value.email, uiState.value.password)
            result.onSuccess {
                uiState.value = uiState.value.copy(isLoading = false, error = null, isRegistered = true)
            }
            result.onFailure { exception ->
                uiState.value = uiState.value.copy(isLoading = false, error = exception.message, isRegistered = false)
            }
        }
    }
}