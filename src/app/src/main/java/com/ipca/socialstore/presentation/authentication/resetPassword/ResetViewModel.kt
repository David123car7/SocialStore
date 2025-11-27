package com.ipca.socialstore.presentation.authentication.resetPassword

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.login.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ResetState (
    var email : String = "",
    var error : String? = null,
    var isLoading : Boolean = false,
)

@HiltViewModel
class ResetViewModel @Inject constructor(private val resetPasswordUseCase: ResetPasswordUseCase): ViewModel(){

    var uiState = mutableStateOf(ResetState())

    fun updateEmail(email : String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun resetPassword() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = resetPasswordUseCase(uiState.value.email)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}