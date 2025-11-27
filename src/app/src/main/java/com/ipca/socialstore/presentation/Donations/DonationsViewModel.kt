package com.ipca.socialstore.presentation.donations

import android.content.ClipData
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donations.AddDonationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class DonationState(
    val donation: DonationModel? = DonationModel(),
    val error : String? = null,
    val isLoading : Boolean = false,
    val itemsList : List<ItemModel> ?= emptyList()
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

    fun updateItem(itemName: String, itemQuantity: Int){

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
