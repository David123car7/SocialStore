package com.ipca.socialstore.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.login.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeState (
    var error : String? = null,
    var isLoading : Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val logoutUseCase: LogoutUseCase): ViewModel() {
    var uiState = mutableStateOf(HomeState())

    fun logout(){
        val result = logoutUseCase()
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