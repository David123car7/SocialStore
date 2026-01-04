package com.ipca.socialstore.presentation.views.Scheduling.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetAllBeneficiaryUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch


data class CreateSchedulingState(
    val beneficiaries : List<BeneficiaryModel> ?= null,
    val isLoading : Boolean? = false,
    val error : ErrorText? = null
)

@HiltViewModel
class CreateSchedulingViewModel @Inject constructor(
    private val getAllBeneficiaryUseCase: GetAllBeneficiaryUseCase
) : ViewModel(){

    val uiState = mutableStateOf(CreateSchedulingState())




    /*
    fun createScheduling(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val createResult = createSchedulingUseCase(uiState.value.beneficiaryId!!,uiState.value.schedulingDate!!,uiState.value.notification!!)
            when(createResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                    )
                }
                is ResultWrapper.Error ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = createResult.error.asUiText(),
                    )
                }
            }
        }
    }

     */
}

