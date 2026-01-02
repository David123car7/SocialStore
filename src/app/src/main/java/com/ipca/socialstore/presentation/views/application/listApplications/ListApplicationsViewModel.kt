package com.ipca.socialstore.presentation.views.application.listApplications

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.application.GetAllAplicationsService
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListApplicationsState(
    val applications: List<ApplicationModelReceiver> = emptyList(),
    val isLoading: Boolean = false,
    val error: ErrorText? = null,
)

@HiltViewModel
class ListApplicationsViewModel @Inject constructor(
    private val getAllAplicationsService: GetAllAplicationsService): ViewModel(){
    var uiState = mutableStateOf(ListApplicationsState())

    init {
        getAllApplications()
    }

    fun getAllApplications(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = getAllAplicationsService()
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        applications = result.data
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