package com.ipca.socialstore.presentation.views.home.beneficiaryHome

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class BeneficiaryHomeState(
    val name: String = "",
    val isLoading : Boolean ?= false,
    val error : ErrorText? = null,
)

@HiltViewModel
class BeneficiaryHomeViewModel @Inject constructor(): ViewModel() {
    val uiState = mutableStateOf(BeneficiaryHomeState())


}