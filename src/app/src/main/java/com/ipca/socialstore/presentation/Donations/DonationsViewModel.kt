package com.ipca.socialstore.presentation.Donations

import androidx.compose.runtime.mutableStateOf
import androidx.credentials.webauthn.Cbor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipca.socialstore.Models.DonationModel
import com.ipca.socialstore.data.repository.ResultWrapper
import com.ipca.socialstore.domain.login.AddDonationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

data class DonationState(
    val donation: DonationModel? = DonationModel(),
    val items :String? = null,
    val quantity : String? = null,
    val error : String? = null,
    val isLoading : Boolean = false
)

@HiltViewModel
class DonationsViewModel @Inject constructor(
    private val addDonationUseCase: AddDonationUseCase
): ViewModel(){
    var uiState = mutableStateOf(DonationState())

    fun updateCampaign(campaign : String ){
        val updateDonation = uiState.value.donation?.copy(
            campaign = campaign
        )
        uiState.value = uiState.value.copy(
            donation = updateDonation
        )
    }

    fun updateDonationData(date : Date){
        uiState.value.donation?.copy(
            donationData = date
        )
    }

    fun updateDonor(donor : String){
        uiState.value.donation?.copy(
            donorName = donor
        )
    }

    fun updateItem(item: String, quantity: String){
        val updateMap = uiState.value.donation?.donatedItems?.toMutableMap() ?: mutableMapOf()
        updateMap[item] = quantity

        val updateDonation = uiState.value.donation?.copy(
            donatedItems = updateMap
        )

        uiState.value = uiState.value.copy(donation = updateDonation)
    }

    fun addDonation(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )
        viewModelScope.launch {
            val result = addDonationUseCase(uiState.value.donation)
            when (result){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                }
                is ResultWrapper.Error ->{
                    uiState.value = uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                else -> {}
            }
        }

    }
}
