package com.ipca.socialstore.presentation.views.Scheduling.schedulingConfirmation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.scheduling.AcceptSchedulingDateUseCase
import com.ipca.socialstore.domain.scheduling.DeclineSchedulingDateUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByIdUseCase
import com.ipca.socialstore.domain.scheduling.UpdateNoteUseCase
import com.ipca.socialstore.domain.scheduling.UpdateReasonUseCase
import com.ipca.socialstore.domain.services.beneficiary.GetBeneficiaryBySchedulingIdServiceUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.views.mockups.Beneficiary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchedulingConfirmationState(
    val scheduling : SchedulingModel? = null,
    val beneficiary: BeneficiaryModel? = null,
    val isLoading : Boolean? = false,
    val note : String? = null,
    val error : ErrorText? = null,
)

@HiltViewModel
class SchedulingConfirmationViewModel @Inject constructor(
    private val getBeneficiaryBySchedulingIdServiceUseCase: GetBeneficiaryBySchedulingIdServiceUseCase,
    private val getSchedulingByIdUseCase: GetSchedulingByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val acceptSchedulingDateUseCase: AcceptSchedulingDateUseCase,
    private val declineSchedulingDateUseCase: DeclineSchedulingDateUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    val uiState = mutableStateOf(SchedulingConfirmationState())

    private val schedulingId: String? = savedStateHandle["schedulingId"]

    init {
        getBeneficiary()
        getScheduling()
    }
    fun getBeneficiary(){
        val id = schedulingId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = ErrorText.DynamicString("Erro ao encontrar Beneficiário"))
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
            uiState.value = uiState.value.copy(error = ErrorText.DynamicString("Erro ao encontrar Beneficiário"))
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getSchedulingByIdUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling =  result.data,
                    isLoading = false,
                    note = result.data.note
                )
            }
        }
    }

    fun updateNote(){
        val id = schedulingId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = ErrorText.DynamicString("Erro ao encontrar Beneficiário"))
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = updateNoteUseCase(id, uiState.value.note!!)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling =  result.data,
                    isLoading = false,
                    note = result.data.note
                )
            }
        }
    }

    fun updateNoteUi(note : String){
        uiState.value = uiState.value.copy(
            note = note
        )
    }



    fun acceptScheduling(){
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = acceptSchedulingDateUseCase(uiState.value.scheduling?.id!!,uiState.value.note!!)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling =  result.data,
                    isLoading = false,
                )
            }
        }
    }

    fun declineScheduling(){
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = declineSchedulingDateUseCase(uiState.value.scheduling?.id!!, uiState.value.note!!)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling =  result.data,
                    isLoading = false,
                )
            }
        }
    }



}

