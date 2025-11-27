package com.ipca.socialstore.presentation.authentication.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.register.RegisterUseCase
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
            val result = registerUseCase(uiState.value.email, uiState.value.password)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isRegistered = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isRegistered = false,
                        error = result.message
                    )
                }
            }
        }
    }
}