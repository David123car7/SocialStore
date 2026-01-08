package com.ipca.socialstore.presentation.views.donation.listAllDonations

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.GetAllCampaignsUseCase
import com.ipca.socialstore.domain.donation.GetAllDonationUseCase
import com.ipca.socialstore.presentation.models.DonationHelperModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.views.donation.create.DonationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ListDonationsState(
    val donations : List<DonationModel>? = emptyList(),
    val campaigns : List<CampaignModel>? = emptyList(),
    val error : ErrorText? = null,
    val isLoading : Boolean? = null,
    val filterDonations : List<DonationHelperModel>? = null,
    val joinedDonations: List<DonationHelperModel> = emptyList(),
)
@HiltViewModel
class ListAllDonationsViewModel @Inject constructor(
    private val getAllDonationUseCase: GetAllDonationUseCase,
    private val getAllCampaignsUseCase: GetAllCampaignsUseCase
): ViewModel() {

    val uiState = mutableStateOf(ListDonationsState())

    init {
        getDonations()
        getCampaigns()

    }

    fun getCampaigns() {
        viewModelScope.launch {
            val result = getAllCampaignsUseCase()
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(campaigns = result.data)
                combineDonationsWithCampaigns()
            }
            if (result is ResultWrapper.Error){
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = result.error.asUiText()
                )
            }
        }
    }

    fun getDonations() {
        uiState.value = uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = getAllDonationUseCase()
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(donations = result.data, isLoading = false)
                combineDonationsWithCampaigns()
            }
            if (result is ResultWrapper.Error){
            uiState.value = uiState.value.copy(
                isLoading = false,
                error = result.error.asUiText()
            )
        }
        }
    }

    private fun combineDonationsWithCampaigns() {
        val currentDonations = uiState.value.donations ?: return
        val currentCampaigns = uiState.value.campaigns ?: return

        val joined = currentDonations.map { donation ->
            val campaignName = currentCampaigns.find { it.id == donation.campaignId }?.name ?: "Campanha ID: ${donation.campaignId}"
            DonationHelperModel(campaignName = campaignName, donation = donation)
        }
        uiState.value = uiState.value.copy(joinedDonations = joined)
    }

    fun filterDonationsByCampaign(campaignId: Int) {
        val result = uiState.value.joinedDonations.filter { it.donation.campaignId == campaignId }
        uiState.value = uiState.value.copy(filterDonations = result)
    }
}