package com.ipca.socialstore.presentation.home.defaultHome

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.profile.GetUserRoleUseCase
import com.ipca.socialstore.domain.auth.LogoutUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DefaultHomeState (
    var error : ErrorText? = null,
    var userRole: String = "",
    var isLoading : Boolean = false,
)

@HiltViewModel
class DefaultHomeViewModel @Inject constructor(private val logoutUseCase: LogoutUseCase, private val getUserRoleUseCase: GetUserRoleUseCase): ViewModel() {
    var uiState = mutableStateOf(DefaultHomeState())

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
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }
}