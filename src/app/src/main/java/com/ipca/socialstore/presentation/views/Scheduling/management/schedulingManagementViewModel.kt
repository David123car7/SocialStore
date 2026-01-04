package com.ipca.socialstore.presentation.views.Scheduling.management

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetAllBeneficiaryUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SchedulingManagementState(
    val beneficiaries : List<BeneficiaryModel>? = null,
    val filteredBeneficiaries: List<BeneficiaryModel>? = null,
    val isLoading : Boolean? = false,
    val error : ErrorText? = null
)

@HiltViewModel
class SchedulingManagementViewModel @Inject constructor(private val getAllBeneficiaryUseCase: GetAllBeneficiaryUseCase) : ViewModel(){

    val uiState = mutableStateOf(SchedulingManagementState())



    fun getAllBeneficiaries(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = getAllBeneficiaryUseCase()
            when(result){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        beneficiaries = result.data,
                        filteredBeneficiaries = result.data
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

    fun onSearchBeneficiary(query : String) {
        println("Query $query")
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
        println(uiState.value.filteredBeneficiaries)
    }
}
