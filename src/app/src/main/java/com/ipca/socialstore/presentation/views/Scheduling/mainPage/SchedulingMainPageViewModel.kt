package com.ipca.socialstore.presentation.views.Scheduling.mainPage

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.domain.beneficiary.GetAllBeneficiaryUseCase
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.scheduling.CancelSchedulingAdminUseCase
import com.ipca.socialstore.domain.services.scheduling.CreateSchedulingServiceUseCase
import com.ipca.socialstore.domain.services.scheduling.GetSchedulingInfoByMonthUseCase
import com.ipca.socialstore.presentation.models.SchedulingReceiverModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchedulingMainState(
    val scheduling : List<SchedulingReceiverModel>? = null,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val beneficiary: BeneficiaryModel? = null,
    val listBeneficiary : List<BeneficiaryModel> ? = emptyList(),
    val newScheduling : SchedulingModel = SchedulingModel(null,"",-1,"",null,""),
)

@HiltViewModel
class SchedulingMainPageViewModel @Inject constructor(
    private val getSchedulingInfoByMonthUseCase: GetSchedulingInfoByMonthUseCase,
    private val cancelSchedulingAdminUseCase: CancelSchedulingAdminUseCase,
    private val getBeneficiaryByIdUseCase: GetBeneficiaryByIdUseCase,
    private val getAllBeneficiaryUseCase: GetAllBeneficiaryUseCase,
    private val createSchedulingServiceUseCase: CreateSchedulingServiceUseCase,
    savedStateHandle: SavedStateHandle,
): ViewModel()
{
    val uiState = mutableStateOf(SchedulingMainState())

    private val beneficiaryId: String? = savedStateHandle["beneficiaryId"]

    init {
        val id = if (beneficiaryId != null) beneficiaryId.toIntOrNull() else null

        id?.let {
            fetchBeneficiary(it)
        }

    }
    private fun fetchBeneficiary(id : Int) {
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
                        error = result.error.asUiText(),
                        isLoading = false
                    )
                }
            }
        }
    }
    fun getAllBeneficiariesByMonth(month : Int, year : Int){
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
                        scheduling = result.data
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
                    val currentList = uiState.value.scheduling
                    val newList = currentList?.filter { it.schedulingId != id }
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        scheduling = newList
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

    fun getAllBeneficiary() {
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getAllBeneficiaryUseCase()
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        listBeneficiary = result.data,
                        isLoading = false
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        error = result.error.asUiText(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun createScheduling(){
        val scheduling = uiState.value.newScheduling
        if (scheduling.id == -1){
            uiState.value = uiState.value.copy(
                error = ErrorText.DynamicString("Selecione um Beneficiário Válido")
            )
        }
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            println("EStou AQui ${uiState.value.newScheduling}")
            val result = createSchedulingServiceUseCase(uiState.value.newScheduling)
            when(result){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
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

    fun updateSchedulingDate(date : String){
        val newDate = uiState.value.newScheduling.copy(
            schedulingDate = date
        )
        uiState.value = uiState.value.copy(
            newScheduling = newDate
        )
    }

    fun updateBeneficiaryId(beneficiaryId : Int){
        val newId = uiState.value.newScheduling.copy(
            beneficiaryId = beneficiaryId
        )
        uiState.value = uiState.value.copy(
             newScheduling = newId
        )
    }
}