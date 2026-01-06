package com.ipca.socialstore.presentation.views.home.beneficiaryHome

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.beneficiary.GetBeneficiaryByUidUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.views.beneficiary.profile.createEmptyBeneficiary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BeneficiaryHomeState(
    val beneficiary: BeneficiaryModel = createEmptyBeneficiary(),
    val isLoading : Boolean ?= false,
    val error : ErrorText? = null,
)

@HiltViewModel
class BeneficiaryHomeViewModel @Inject constructor(
    private val getBeneficiaryByUidUseCase: GetBeneficiaryByUidUseCase): ViewModel() {
    val uiState = mutableStateOf(BeneficiaryHomeState())

    init {
        getBeneficiary()
    }

    fun getBeneficiary(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = getBeneficiaryByUidUseCase()
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        beneficiary = result.data
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