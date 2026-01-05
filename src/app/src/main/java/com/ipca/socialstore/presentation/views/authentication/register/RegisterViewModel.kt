package com.ipca.socialstore.presentation.views.authentication.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.RegisterUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState (
    var email : String = "",
    var password : String = "",
    var error : ErrorText? = null,
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
            when(val result = registerUseCase(uiState.value.email, uiState.value.password)){
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
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }
}