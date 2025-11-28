package com.ipca.socialstore.presentation.authentication.resetPassword

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordState (
    var email : String = "",
    var password : String = "",
    var token : String = "",
    var error : String? = null,
    var isLoading : Boolean = false,
    val passwordReseted: Boolean = false
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val resetPasswordUseCase: ResetPasswordUseCase) : ViewModel(){
    var uiState = mutableStateOf(ResetPasswordState())

    fun updateEmail(email : String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun updatePassword(password : String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun updateToken(token : String) {
        uiState.value = uiState.value.copy(token = token)
    }

    fun resetPassword(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = resetPasswordUseCase(uiState.value.email, uiState.value.password, uiState.value.token)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        passwordReseted = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        passwordReseted = false,
                        error = result.message
                    )
                }
            }
        }
    }
}