package com.ipca.socialstore.presentation.views.Scheduling.listAllSchedulingUser

import android.view.View
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.scheduling.GetSchedulingByBeneficiaryIdUseCase
import com.ipca.socialstore.domain.services.beneficiary.GetBeneficiaryByUidUseCase
import com.ipca.socialstore.domain.services.scheduling.GetAllSchedulingUserUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ListSchedulingUserState(
    val beneficiary : BeneficiaryModel? =null,
    val scheduling : List<SchedulingModel> = emptyList(),
    val accept : Int? = null,
    val cancel : Int? = null,
    val isLoading : Boolean ?= false,
    val error : AppError? = null,
    val showList : List<SchedulingModel> = emptyList(),
)
@HiltViewModel
class ListAllSchedulingUserViewModel @Inject constructor(
    private val getAllSchedulingUserUseCase: GetAllSchedulingUserUseCase,
    private val getBeneficiaryByUidUseCase: GetBeneficiaryByUidUseCase,
    private val getSchedulingByBeneficiaryIdUseCase: GetSchedulingByBeneficiaryIdUseCase,
    private val getBeneficiaryByIdUseCase: GetBeneficiaryByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    val uiState = mutableStateOf(ListSchedulingUserState())

    private val beneficiaryId: String? = savedStateHandle["beneficiaryId"]


    init {
        if (beneficiaryId == null || beneficiaryId == "null") {
            getBeneficiary()
        } else {
            getBeneficiaryById()
        }
    }

    fun fetchInfo(){
        println(beneficiaryId)
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getAllSchedulingUserUseCase()
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling = result.data.scheduling,
                    accept = result.data.accept,
                    cancel = result.data.cancel
                )
            }
            selectListAccept()
        }
    }

    fun getBeneficiaryById(){
        val id = beneficiaryId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getBeneficiaryByIdUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    beneficiary = result.data
                )
            }
        }
        fetchInfoById()
    }

    fun fetchInfoById(){
        println(beneficiaryId)
        val id = beneficiaryId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = AppError.ParseError)
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getSchedulingByBeneficiaryIdUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling = result.data,
                )
            }
            selectListAccept()
        }
    }



    fun getBeneficiary(){
        println("estou ca")
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getBeneficiaryByUidUseCase()
            if (result is ResultWrapper.Success) {
                println("data:${result.data}")
                uiState.value = uiState.value.copy(
                    beneficiary = result.data
                )
            }
            fetchInfo()
        }
    }

    fun selectListAccept(){
        val schedules = uiState.value.scheduling
        uiState.value = uiState.value.copy(
            showList = schedules.filter { it.state == "accept" },
        )
    }

    fun selectListHistory(){
        val schedules = uiState.value.scheduling
        uiState.value = uiState.value.copy(
            showList = schedules.filter { it.state!= "decline" },
        )
    }

    fun selectListCanceled(){
        val schedules = uiState.value.scheduling
        uiState.value = uiState.value.copy(
            showList = schedules.filter { it.state == "canceled" },
        )
    }

    fun selectListInProgress(){
        val schedules = uiState.value.scheduling
        uiState.value = uiState.value.copy(
            showList = schedules.filter { it.state == "in_Progress" },
        )
    }





}