package com.ipca.socialstore.presentation.views.campaign.adminList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.DeleteCampaignUseCase
import com.ipca.socialstore.domain.campaign.GetAllCampaignsUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaignsListState(
    val campaigns : List<CampaignModel> = emptyList(),
    val isLoading : Boolean = false,
    val error : ErrorText? = null,
)

@HiltViewModel
class CampaignsListViewModel @Inject constructor(
    private val getAllCampaignsUseCase: GetAllCampaignsUseCase): ViewModel(){
    val uiState = mutableStateOf(CampaignsListState())

    init {
        getAllCampaigns()
    }

    fun getAllCampaigns(){
        viewModelScope.launch {
            when(val result = getAllCampaignsUseCase()){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        campaigns = result.data
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