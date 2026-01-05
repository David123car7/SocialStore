package com.ipca.socialstore.presentation.views.beneficiary

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.scheduling.CancelSchedulingAdminUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByBeneficiaryIdUseCase
import com.ipca.socialstore.domain.services.scheduling.GetAllInfoBeneficiaryUseCase
import com.ipca.socialstore.presentation.models.SchedulingHelperModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BeneficiaryManagementState(
    val beneficiary : BeneficiaryModel? = null,
    val isLoading : Boolean ?= false,
    val error : AppError? = null,
    val scheduling : List<SchedulingModel>? = null,
    val accept : Int? = null,
    val cancel : Int? = null,
    val nextScheduling : SchedulingModel? = null,
    val note : String? = null,
    val declined : List<SchedulingModel>? = null,
)
@HiltViewModel
class BeneficiaryManagementViewModel @Inject constructor(
    private val getBeneficiaryByIdUseCase: GetBeneficiaryByIdUseCase,
    private val getAllInfoBeneficiaryUseCase: GetAllInfoBeneficiaryUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    val uiState = mutableStateOf(BeneficiaryManagementState())

    private val beneficiaryId: String? = savedStateHandle["beneficiaryId"]

    init {
        fetchBeneficiary()
        fetchInfo()

    }

    private fun fetchBeneficiary() {
        val id = beneficiaryId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getBeneficiaryByIdUseCase(id)
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        beneficiary = result.data,
                        isLoading = false
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        error = result.error,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun fetchInfo(){
        val id = beneficiaryId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getAllInfoBeneficiaryUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling = result.data.scheduling,
                    accept = result.data.accept,
                    cancel = result.data.cancel
                )
            }
            getNextScheduling()
            getDeclined()
        }
    }

    private fun getNextScheduling(){
        val scheduling = uiState.value.scheduling
        val next = scheduling?.filter { it.state == "accept" }?.minByOrNull { it.schedulingDate }
        uiState.value = uiState.value.copy(
            nextScheduling = next,
            note = next?.note
        )
    }

    fun getDeclined(){
        val scheduling = uiState.value.scheduling
        val declined = scheduling?.filter { it.state == "decline" || it.state == "in_Progress" }

        uiState.value = uiState.value.copy(
            declined = declined
        )
    }



}