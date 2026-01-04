package com.ipca.socialstore.presentation.views.Scheduling.mainPage

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetAllBeneficiaryUseCase
import com.ipca.socialstore.domain.scheduling.CancelSchedulingAdminUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByMonthUseCase
import com.ipca.socialstore.domain.services.scheduling.GetSchedulingInfoByMonthUseCase
import com.ipca.socialstore.presentation.models.SchedulingReceiverModel
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchedulingMainState(
    val beneficiaries : List<SchedulingReceiverModel>? = null,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
)

@HiltViewModel
class SchedulingMainPageViewModel @Inject constructor(
    private val getSchedulingInfoByMonthUseCase: GetSchedulingInfoByMonthUseCase,
    private val cancelSchedulingAdminUseCase: CancelSchedulingAdminUseCase
): ViewModel()
{
    val uiState = mutableStateOf(SchedulingMainState())

    fun getAllBeneficiaries(month : Int, year : Int){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = getSchedulingInfoByMonthUseCase(month,year)
            when(result){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        beneficiaries = result.data
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText(),
                    )
                }
            }
        }
    }

    fun cancelScheduling(id : Int){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = cancelSchedulingAdminUseCase(id)
            when(result){
                is ResultWrapper.Success ->{
                    val currentList = uiState.value.beneficiaries
                    val newList = currentList?.filter { it.schedulingId != id }
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        beneficiaries = newList
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText(),
                    )
                }
            }
        }
    }
}