package com.ipca.socialstore.presentation.views.home.defaultHomeView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.GetAllCampaignsLimitUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DefaultHomeState (
    var error : ErrorText? = null,
    var campaignList: List<CampaignModel> = emptyList(),
    var isLoading : Boolean = false,
)

@HiltViewModel
class DefaultHomeViewModel @Inject constructor(
    private val getAllCampaignsLimitUseCase: GetAllCampaignsLimitUseCase
): ViewModel() {
    var uiState = mutableStateOf(DefaultHomeState())

    init {
        getAllCampaigns(limit = 3)
    }

    fun getAllCampaigns(limit: Int){
        viewModelScope.launch {
            when(val result = getAllCampaignsLimitUseCase(limit = limit)){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        campaignList = result.data
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