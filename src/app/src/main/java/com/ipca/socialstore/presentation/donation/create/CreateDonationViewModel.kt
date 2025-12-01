package com.ipca.socialstore.presentation.donation.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donation.CreateDonationUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationState(
    val donation : DonationModel = DonationModel("",""),
    val error : ErrorText? = null,
    val isLoading : Boolean? = null,
    val isCreated : Boolean? = false
)
@HiltViewModel
class CreateDonationViewModel @Inject constructor(private val createDonationUseCase: CreateDonationUseCase) : ViewModel(){

    val uiState = mutableStateOf(DonationState())


    fun updateDonationDate(date : String){
        val donation = uiState.value.donation.copy(
            donationDate = date
        )
        uiState.value = uiState.value.copy(
            donation = donation
        )
    }

    /*
    fun updateDonationCampaign(campaign : String){
        val donation = uiState.value.donation.copy(
            campaign = campaign
        )
        uiState.value = uiState.value.copy(
            donation = donation
        )
    }
    */

    fun updateCampaignId(){
        val id = 44
        val donation = uiState.value.donation.copy(
            campaignId = id.toString()
        )
        uiState.value = uiState.value.copy(
            donation = donation
        )
    }

    fun createDonation(){

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
            isCreated = false
        )
        updateCampaignId()
        viewModelScope.launch {
            val result = createDonationUseCase(uiState.value.donation)
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
                        error = result.error.asUiText(),
                        isCreated = false
                    )
                }
            }
        }
    }
}