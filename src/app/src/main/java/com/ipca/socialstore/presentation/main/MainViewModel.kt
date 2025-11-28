package com.ipca.socialstore.presentation.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.domain.auth.GetUserSessionStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(private val getUserSessionState: GetUserSessionStateUseCase): ViewModel() {
    var sessionState = mutableStateOf(SessionState())

    init {
        viewModelScope.launch {
            getUserStateSession()
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