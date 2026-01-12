package com.ipca.socialstore.presentation.views.authentication.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.GetUserIdUseCase
import com.ipca.socialstore.domain.auth.LoginUseCase
import com.ipca.socialstore.domain.usecases.user.SetUserTokenUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState (
    var email : String = "",
    var password : String = "",
    var error : ErrorText? = null,
    var isLoading : Boolean = false,
    var isLoggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val setUserTokenUseCase: SetUserTokenUseCase): ViewModel() {
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
                    updateToken()
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }

    private fun updateToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Falha ao obter token", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result

            val userResult = getUserIdUseCase()
            if(userResult is ResultWrapper.Success){
                val userId = userResult.data
                viewModelScope.launch {
                    setUserTokenUseCase(userId, token)
                }
                Log.d("FCM", "Token enviado para a BD: $token")
            }
        }
    }
}