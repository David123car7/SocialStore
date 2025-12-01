package com.ipca.socialstore.presentation.authentication.requestResetPassword

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.RequestResetPasswordUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class RequestResetState (
    var email : String = "",
    var error : ErrorText? = null,
    var isLoading : Boolean = false,
    val resetRequested: Boolean = false
)

@HiltViewModel
class RequestResetPasswordViewModel @Inject constructor(private val requestResetPasswordUseCase: RequestResetPasswordUseCase): ViewModel(){

    var uiState = mutableStateOf(RequestResetState())

    fun updateEmail(email : String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun requestResetPassword() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = requestResetPasswordUseCase(uiState.value.email)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        resetRequested = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        resetRequested = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }
}