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
import com.ipca.socialstore.domain.scheduling.AcceptJustificationUseCase
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
    val isSuccess : Boolean = false
)

@HiltViewModel
class JustificationScreenViewModel @Inject constructor(
    private val updateReasonUseCase: UpdateReasonUseCase,
    private val getBeneficiaryBySchedulingIdServiceUseCase: GetBeneficiaryBySchedulingIdServiceUseCase,
    private val getSchedulingByIdUseCase: GetSchedulingByIdUseCase,
    private val acceptJustificationUseCase: AcceptJustificationUseCase,
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
        val currentReason = uiState.value.reason

        if (id == null || currentReason.isNullOrBlank()) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }

        uiState.value = uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = updateReasonUseCase(id, currentReason)

            when (result) {
                is ResultWrapper.Success -> {
                    println("SUCESSO: Justificação guardada!")
                    uiState.value = uiState.value.copy(
                        scheduling = result.data,
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is ResultWrapper.Error -> {
                    println("ERRO AO GUARDAR: ${result.error}")
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error
                    )
                }
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

    fun onAdminAccept() {
        val id = schedulingId?.toIntOrNull() ?: return
        viewModelScope.launch {
            val result = acceptJustificationUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(isSuccess = true)
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