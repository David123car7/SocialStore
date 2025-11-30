package com.ipca.socialstore.presentation.campaign.listAll

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.GetAllCampaignsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GetAllCampaignsState(
    val campaigns : List<CampaignModel> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)
@HiltViewModel
class ListAllCampaignsViewModel @Inject constructor(private val getAllCampaignsUseCase: GetAllCampaignsUseCase): ViewModel(){

    val uiState = mutableStateOf(GetAllCampaignsState())


    fun getAllCampaigns(){
        viewModelScope.launch {
            val result = getAllCampaignsUseCase()

            when(result){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                        campaigns = result.data!!
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }

}