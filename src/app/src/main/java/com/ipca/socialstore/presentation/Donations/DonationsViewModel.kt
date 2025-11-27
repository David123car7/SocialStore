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
)

data class ItemInput(
    val item: ItemModel? = null,
    val quantity: Int = 0
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

    fun addItem(item: ItemModel?) {
        val productName = item?.itemName ?: return

        val currentDonation = uiState.value.donation ?: DonationModel()

        val currentMap = (currentDonation.donatedItems ?: emptyMap()).toMutableMap()

        val currentQuantity = currentMap[productName] ?: 0
        currentMap[productName] = currentQuantity + 1

        val updatedDonation = currentDonation.copy(donatedItems = currentMap)
        uiState.value = uiState.value.copy(
            donation = updatedDonation
        )
    }

    fun addDonation(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        uiState.value = uiState.value.copy(donation = uiState.value.donation)

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
