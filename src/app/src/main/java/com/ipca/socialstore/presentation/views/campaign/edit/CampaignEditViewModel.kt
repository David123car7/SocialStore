package com.ipca.socialstore.presentation.views.campaign.edit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.GetCampaignByIdUseCase
import com.ipca.socialstore.domain.campaign.UpdateCampaignUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.views.campaign.create.createEmptyCampaign
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditCampaignState(
    val campaign: CampaignModel = createEmptyCampaign(),

    var error : ErrorText? = null,
    var isLoading : Boolean = false,
    var isEdited : Boolean = false,
)

@HiltViewModel
class EditCampaignViewModel @Inject constructor(
    val updateCampaignUseCase: UpdateCampaignUseCase,
    val getCampaignByIdUseCase: GetCampaignByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(){
    var uiState = mutableStateOf(EditCampaignState())

    val campaignId: String? = savedStateHandle["campaign_id"]

    init {
        if(!campaignId.isNullOrEmpty()){
            viewModelScope.launch {
                getCampaign(campaignId.toInt())
            }
        }
    }

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

    fun getCampaign(id: Int){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = getCampaignByIdUseCase(id = id)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        campaign = result.data
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

    fun editCampaign(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = updateCampaignUseCase(uiState.value.campaign)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isEdited = true
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
