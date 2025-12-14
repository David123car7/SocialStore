package com.ipca.socialstore.presentation.views.Scheduling

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.NotificationSchedulingModel
import com.ipca.socialstore.data.models.SchedulingDateModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.scheduling.CreateSchedulingUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class CreateSchedulingState(
    val schedulingDate : SchedulingDateModel = SchedulingDateModel(null,"",),
    val notification : NotificationSchedulingModel? = NotificationSchedulingModel(null,"",""),
    val beneficiaryId : String? = null,
    val isLoading : Boolean? = false,
    val error : ErrorText? = null
)
@HiltViewModel
class CreateSchedulingView @Inject constructor(private val createSchedulingUseCase: CreateSchedulingUseCase) : ViewModel(){

    val uiState = mutableStateOf(CreateSchedulingState())

    fun updateDate(value : String){
        val date = uiState.value.schedulingDate.copy(date = value)

        uiState.value = uiState.value.copy(
            schedulingDate = date
        )
    }

    fun updateCreateAt(){
        val currentTimeMillis = System.currentTimeMillis()
        val date = currentTimeMillis.toString()
        val create = uiState.value.notification?.copy(createdAt = date)

        uiState.value = uiState.value.copy(
            notification = create
        )
    }

    fun updateSubject(value : String){
        val notification = uiState.value.notification?.copy(subject = value)

        uiState.value = uiState.value.copy(
            notification = notification
        )
    }

    fun updateBeneficiaryId(id : String){


        uiState.value = uiState.value.copy(
            beneficiaryId = id
        )
    }

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
}