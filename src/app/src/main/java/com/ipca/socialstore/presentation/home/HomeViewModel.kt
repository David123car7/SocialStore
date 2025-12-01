package com.ipca.socialstore.presentation.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.profile.GetUserRoleUseCase
import com.ipca.socialstore.domain.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState (
    var error : String? = null,
    var userRole: String = "",
    var isLoading : Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val logoutUseCase: LogoutUseCase, private val getUserRoleUseCase: GetUserRoleUseCase): ViewModel() {
    var uiState = mutableStateOf(HomeState())

    fun logout(){
        viewModelScope.launch {
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
}