package com.ipca.socialstore.presentation.views.Scheduling.cancelByUser

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryBySchedulingUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByIdUseCase
import com.ipca.socialstore.domain.scheduling.UpdateReasonUseCase
import com.ipca.socialstore.domain.services.beneficiary.GetBeneficiaryBySchedulingIdServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JustificationState(
    val scheduling : SchedulingModel? = null,
    val beneficiary : BeneficiaryModel? = null,
    val isLoading : Boolean ?= false,
    val error : AppError? = null,
    val reason : String? = null,
)

@HiltViewModel
class JustificationScreenViewModel @Inject constructor(
    private val updateReasonUseCase: UpdateReasonUseCase,
    private val getBeneficiaryBySchedulingIdServiceUseCase: GetBeneficiaryBySchedulingIdServiceUseCase,
    private val getSchedulingByIdUseCase: GetSchedulingByIdUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    val uiState = mutableStateOf(JustificationState())

    private val schedulingId: String? = savedStateHandle["schedulingId"]

    init {
        getBeneficiary()
        getScheduling()
    }
    fun updateReasonUi(reason : String){
        uiState.value = uiState.value.copy(
            reason = reason
        )
    }

    fun updateReason(){
        val id = schedulingId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = updateReasonUseCase(id, uiState.value.reason!!)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling = result.data,
                    isLoading = false
                )
            }
        }
    }

    fun getBeneficiary(){
        val id = schedulingId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getBeneficiaryBySchedulingIdServiceUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    beneficiary =  result.data,
                    isLoading = false
                )
            }
        }
    }

    fun getScheduling(){
        val id = schedulingId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getSchedulingByIdUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling =  result.data,
                    isLoading = false,
                    reason = result.data.reason
                )
            }
        }
    }

}