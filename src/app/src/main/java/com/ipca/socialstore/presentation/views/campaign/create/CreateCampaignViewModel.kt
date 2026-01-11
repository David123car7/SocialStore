package com.ipca.socialstore.presentation.views.campaign.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.CreateCampaignUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateCampaignState(
    val campaign: CampaignModel = createEmptyCampaign(),

    var error : ErrorText? = null,
    var isLoading : Boolean = false,
    var isCreated : Boolean = false,
)

@HiltViewModel
class CreateCampaignViewModel @Inject constructor(
    val createCampaignUseCase: CreateCampaignUseCase
) : ViewModel(){
    var uiState = mutableStateOf(CreateCampaignState())

    fun updateName(name: String) {
        uiState.value = uiState.value.copy(
            campaign = uiState.value.campaign.copy(name = name)
        )
    }

    fun updateDescription(description: String) {
        uiState.value = uiState.value.copy(
            campaign = uiState.value.campaign.copy(description = description)
        )
    }

    fun updateCategory(category: String) {
        uiState.value = uiState.value.copy(
            campaign = uiState.value.campaign.copy(category = category)
        )
    }

    fun updateStartDate(startDate: String) {
        uiState.value = uiState.value.copy(
            campaign = uiState.value.campaign.copy(startDate = startDate)
        )
    }

    fun updateEndDate(endDate: String) {
        uiState.value = uiState.value.copy(
            campaign = uiState.value.campaign.copy(endDate = endDate)
        )
    }

    fun createCampaign(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

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
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }
}

fun createEmptyCampaign(): CampaignModel {
    return CampaignModel(
        id = null,
        name = "",
        description = "",
        category = "",
        onGoing = true, // Default to not started
        startDate = "",  // Or use LocalDate.now().toString()
        endDate = "",
        goal = 100,
        currentDonations = 0
    )
}