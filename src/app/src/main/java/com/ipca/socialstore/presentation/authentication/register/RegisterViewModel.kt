package com.ipca.socialstore.presentation.authentication.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.models.isValid
import com.ipca.socialstore.data.objects.UserRoles
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState (
    var email : String = "",
    var password : String = "",
    var user: UserModel = UserModel(uid = null,firstName = "", lastName = "", addressId = null, birthDate = "", role = UserRoles.default),
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

    fun updateFirstName(firstName: String) {
        uiState.value = uiState.value.copy(
            user = uiState.value.user.copy(firstName = firstName)
        )
    }

    fun updateLastName(lastName: String) {
        uiState.value = uiState.value.copy(
            user = uiState.value.user.copy(lastName = lastName)
        )
    }

    fun updateBirthDate(birthDate: String) {
        uiState.value = uiState.value.copy(
            user = uiState.value.user.copy(birthDate = birthDate)
        )
    }

    fun updatePassword(password : String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun register(){
        if(!uiState.value.user.isValid()) // why "!!" if i verify first that the user is not null???
            return

        viewModelScope.launch {
            val result = registerUseCase(uiState.value.email, uiState.value.password, uiState.value.user)
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