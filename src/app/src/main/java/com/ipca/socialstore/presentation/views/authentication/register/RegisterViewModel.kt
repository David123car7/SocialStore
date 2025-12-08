package com.ipca.socialstore.presentation.views.authentication.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.ProfileModel
import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.models.isValid
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.RegisterUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState (
    var email : String = "",
    var password : String = "",
    val profile: ProfileModel = ProfileModel(name = "", birthDate = ""),
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

    fun updateName(name: String) {
        uiState.value = uiState.value.copy(
            profile = uiState.value.profile.copy(name = name)
        )
    }

    fun updateBirthDate(birthDate: String) {
        uiState.value = uiState.value.copy(
            profile = uiState.value.profile.copy(birthDate = birthDate)
        )
    }

    fun updatePassword(password : String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun register(){
        viewModelScope.launch {
            val result = registerUseCase(uiState.value.email, uiState.value.password, uiState.value.profile)
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
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }
}