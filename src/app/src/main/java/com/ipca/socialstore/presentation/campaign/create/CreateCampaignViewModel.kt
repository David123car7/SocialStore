package com.ipca.socialstore.presentation.campaign.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.CreateCampaignUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject


data class CampaignState(
    val campaign : CampaignModel = CampaignModel("",""),
    val isLoading : Boolean = false,
    val error: String? = null,
    val isCreated : Boolean  = false
)

@HiltViewModel
class CreateCampaingViewModel @Inject constructor(private val createCampaignUseCase: CreateCampaignUseCase) : ViewModel(){

    val uiState = mutableStateOf(CampaignState())

    fun updateCampaignName(name : String){
        val campaign = uiState.value.campaign.copy(
            name = name
        )
        uiState.value = uiState.value.copy(
            campaign = campaign
        )
    }

    fun updateDate(date: String){
        val campaign = uiState.value.campaign.copy(
            date = date
        )
        uiState.value = uiState.value.copy(
            campaign = campaign
        )
    }


    fun createCampaign(){

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
            isCreated = false
        )

        viewModelScope.launch {
            val result = createCampaignUseCase(uiState.value.campaign)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isCreated = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isCreated = false
                    )
                }
            }
        }
    }
}