package com.ipca.socialstore.presentation.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.profile.GetUserRoleUseCase
import com.ipca.socialstore.domain.auth.GetUserSessionStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    var userRole: UserRole = UserRole.NOROLE,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(private val getUserSessionState: GetUserSessionStateUseCase, private val getUserRoleUseCase: GetUserRoleUseCase): ViewModel() {
    var sessionState = mutableStateOf(SessionState())

    init {
        viewModelScope.launch {
            getUserStateSession()
        }
    }

    suspend fun getUserRole(){
        val result = getUserRoleUseCase()
        when(result){
            is ResultWrapper.Success -> {
                sessionState.value = sessionState.value.copy(
                    isLoading = false,
                    userRole = result.data ?: UserRole.NOROLE
                )
            }
            is ResultWrapper.Error -> {
                sessionState.value = sessionState.value.copy(
                    isLoading = false,
                    error = result.message
                )
            }
        }
    }

    suspend fun getUserStateSession(){
        getUserSessionState().collect { result ->
            when(result){
                is ResultFlowWrapper.Loading -> {
                    sessionState.value = sessionState.value.copy(
                        isLoading = true
                    )
                }
                is ResultFlowWrapper.Success -> {
                    sessionState.value = sessionState.value.copy(
                        isLoading = false,
                        error = null,
                        isLoggedIn = result.data == true
                    )
                    if(sessionState.value.isLoggedIn){
                        getUserRole()
                    }
                    else{
                        sessionState.value = sessionState.value.copy(userRole = UserRole.NOROLE)
                    }
                    Log.d("AppDebug", "The user ROLE is: ${sessionState.value.userRole}")
                    Log.d("AppDebug", "The user STATE is: ${sessionState.value.isLoggedIn}")
                }
                is ResultFlowWrapper.Error -> {
                    sessionState.value = sessionState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isLoggedIn = false
                    )
                }
            }
        }
    }
}