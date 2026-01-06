package com.ipca.socialstore.presentation.views.beneficiary.editProfile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.beneficiary.UpdateBeneficiaryUseCase
import com.ipca.socialstore.domain.services.beneficiary.GetBeneficiaryByUidUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.views.beneficiary.profile.createEmptyBeneficiary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BeneficiaryEditProfileState(
    val beneficiary: BeneficiaryModel = createEmptyBeneficiary(),
    val isLoading : Boolean ?= false,
    val error : ErrorText? = null,
    val isUpdated: Boolean = false
)

@HiltViewModel
class BeneficiaryEditProfileViewModel @Inject constructor(
    private val updateBeneficiaryUseCase: UpdateBeneficiaryUseCase,
    private val getBeneficiaryByUidUseCase: GetBeneficiaryByUidUseCase): ViewModel() {
    val uiState = mutableStateOf(BeneficiaryEditProfileState())

    init {
        getBeneficiary()
    }

    fun updateName(newName: String) {
        uiState.value = uiState.value.copy(
            beneficiary = uiState.value.beneficiary.copy(name = newName)
        )
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        uiState.value = uiState.value.copy(
            beneficiary = uiState.value.beneficiary.copy(phoneNumber = newPhoneNumber)
        )
    }

    fun updateBirthDate(newBirthDate: String) {
        uiState.value = uiState.value.copy(
            beneficiary = uiState.value.beneficiary.copy(birthDate = newBirthDate)
        )
    }

    fun getBeneficiary(){
        viewModelScope.launch {
            val beneficiaryResult = getBeneficiaryByUidUseCase()
            when(beneficiaryResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        beneficiary = beneficiaryResult.data
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = beneficiaryResult.error.asUiText()
                    )
                }
            }
        }
    }

    fun updateBeneficiary(){
        viewModelScope.launch {
            val beneficiaryResult = updateBeneficiaryUseCase(beneficiary = uiState.value.beneficiary)
            when(beneficiaryResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isUpdated = true,
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isUpdated = true,
                        error = beneficiaryResult.error.asUiText()
                    )
                }
            }
        }
    }
}