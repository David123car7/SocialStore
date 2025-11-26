package com.ipca.socialstore.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ipca.socialstore.domain.login.GetUserSessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class HomeState (
    var error : String? = null,
    var isLoading : Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val getUserSessionState: GetUserSessionState): ViewModel() {
    var uiState = mutableStateOf(HomeState())
}