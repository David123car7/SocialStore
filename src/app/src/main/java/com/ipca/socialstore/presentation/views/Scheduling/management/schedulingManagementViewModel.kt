package com.ipca.socialstore.presentation.views.Scheduling.management

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetAllBeneficiaryUseCase
import com.ipca.socialstore.domain.beneficiary.ForgiveAbsenceUseCase // Importa o novo UseCase
import com.ipca.socialstore.domain.beneficiary.SuspendBeneficiaryUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchedulingManagementState(
    val beneficiaries : List<BeneficiaryModel>? = null,
    val filteredBeneficiaries: List<BeneficiaryModel>? = null,
    val isLoading : Boolean? = false,
    val error : ErrorText? = null,
    val showDecisionDialog: Boolean = false,
    val selectedBeneficiary: BeneficiaryModel? = null
)

@HiltViewModel
class SchedulingManagementViewModel @Inject constructor(
    private val getAllBeneficiaryUseCase: GetAllBeneficiaryUseCase,
    private val forgiveAbsenceUseCase: ForgiveAbsenceUseCase,
    private val suspendBeneficiaryUseCase: SuspendBeneficiaryUseCase
) : ViewModel(){

    val uiState = mutableStateOf(SchedulingManagementState())

    init {
        getAllBeneficiaries()
    }

    fun getAllBeneficiaries(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = getAllBeneficiaryUseCase()
            when(result){
                is ResultWrapper.Success ->{
                    val activeBeneficiaries = result.data.filter { it.state != "suspended" }
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        beneficiaries = activeBeneficiaries,
                        filteredBeneficiaries = activeBeneficiaries
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText(),
                    )
                    Log.d("App Debug", "GG: ${result.error}")
                }
            }
        }
    }

    fun openDecisionDialog(beneficiary: BeneficiaryModel) {
        uiState.value = uiState.value.copy(
            selectedBeneficiary = beneficiary,
            showDecisionDialog = true
        )
    }

    fun dismissDialog() {
        uiState.value = uiState.value.copy(
            showDecisionDialog = false,
            selectedBeneficiary = null
        )
    }

    fun forgiveBeneficiary() {
        val beneficiary = uiState.value.selectedBeneficiary ?: return

        uiState.value = uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = forgiveAbsenceUseCase(beneficiary.id!!)
            when(result) {
                is ResultWrapper.Success -> {
                    dismissDialog()
                    getAllBeneficiaries()
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

    fun onSearchBeneficiary(query : String) {
        val listToFilter = uiState.value.beneficiaries ?: emptyList()

        val filtered = if (query.isEmpty()) {
            listToFilter
        } else {
            listToFilter.filter { beneficiary ->
                val matchesName = beneficiary.name.contains(query, ignoreCase = true)
                val matchesProcess = beneficiary.id.toString().contains(query)
                matchesName || matchesProcess
            }
        }

        uiState.value = uiState.value.copy(
            filteredBeneficiaries = filtered,
        )
    }

    fun confirmSuspension() {
        val beneficiary = uiState.value.selectedBeneficiary ?: return
        uiState.value = uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = suspendBeneficiaryUseCase(beneficiary.id!!)

            when(result) {
                is ResultWrapper.Success -> {
                    dismissDialog()
                    getAllBeneficiaries()
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