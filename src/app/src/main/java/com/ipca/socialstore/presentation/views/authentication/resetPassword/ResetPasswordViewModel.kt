package com.ipca.socialstore.presentation.views.authentication.resetPassword

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.RequestResetPasswordUseCase
import com.ipca.socialstore.domain.auth.ResetPasswordUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ResetState (
    var email : String = "",
    var newPassword1 : String = "",
    var newPassword2 : String = "",
    var token : String = "",
    var error : ErrorText? = null,
    var isLoading : Boolean = false,
    val requestedReset: Boolean = false,
    val passwordReseted: Boolean = false
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val requestResetPasswordUseCase: RequestResetPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase): ViewModel(){

    var uiState = mutableStateOf(ResetState())

    fun updateEmail(email : String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun updateToken(token : String) {
        uiState.value = uiState.value.copy(token = token)
    }

    fun updateNewPassword1(password : String) {
        uiState.value = uiState.value.copy(newPassword1 = password)
    }

    fun updateNewPassword2(password : String) {
        uiState.value = uiState.value.copy(newPassword2 = password)
    }

    fun requestResetPassword() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = requestResetPasswordUseCase(uiState.value.email)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        requestedReset = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        requestedReset = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }

    fun resetPassword(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = resetPasswordUseCase(
                uiState.value.email,
                newPassword1 = uiState.value.newPassword1,
                newPassword2 = uiState.value.newPassword2,
                token = uiState.value.token
            )
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
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }
}