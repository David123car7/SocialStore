package com.ipca.socialstore.presentation.views.donation.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel

import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.GetAllCampaignsUseCase

import com.ipca.socialstore.domain.services.donation.CreateDonationServiceUseCase
import com.ipca.socialstore.presentation.models.CreateDonationHelperModel

import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.views.item.ExpirationDate
import com.ipca.socialstore.presentation.views.mockups.Campaign
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationState(
// Campos de rascunho para a Doação (Cabeçalho)
    val donationDate: String = "",
    val donorName: String = "",
    val campaignId: Int? = null,

    // Campos de rascunho para o Item
    val itemName: String = "",
    val itemType: String = "",
    val itemBarCode: String? = null,
    val quantity: Int = 0,
    val expirationDate: String = "",

    // Listas e Estados de Controlo
    val donationHelper: List<CreateDonationHelperModel> = emptyList(),
    val campaigns: List<CampaignModel> = emptyList(),
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
    val error: ErrorText? = null,

    val listDate: List<ExpirationDate> = listOf(ExpirationDate())
)
@HiltViewModel
class CreateDonationViewModel @Inject constructor(
    private val getAllCampaignsUseCase: GetAllCampaignsUseCase,
    private val createDonationServiceUseCase: CreateDonationServiceUseCase,
) : ViewModel(){

    val uiState = mutableStateOf(DonationState())

    fun updateItemName(name: String) {
        uiState.value = uiState.value.copy(itemName = name)
    }

    fun updateItemBarCode(code: String) {
        uiState.value = uiState.value.copy(itemBarCode = code)
    }

    fun updateItemType(type: String) {
        uiState.value = uiState.value.copy(itemType = type)
    }

    fun updateDonationDate(date: String) {
        uiState.value = uiState.value.copy(donationDate = date)
    }

    fun updateDonorName(name: String) {
        uiState.value = uiState.value.copy(donorName = name)
    }

    fun updateCampaignId(id: Int) {
        uiState.value = uiState.value.copy(campaignId = id)
    }

    fun updateExpiration(value : String){
        uiState.value = uiState.value.copy(expirationDate = value)
    }

    fun updateQuantity(value : Int){
        uiState.value = uiState.value.copy(quantity = value)
    }

    fun addItemToDonationHelper() {
        val state = uiState.value
        if (state.itemName.isBlank()) return

        val newEntries = state.listDate
            .filter { it.date.isNotBlank() && it.quantity.isNotBlank() }
            .map { lote ->
                CreateDonationHelperModel(
                    item = ItemModel(
                        name = state.itemName,
                        barCode = state.itemBarCode?.ifBlank { null },
                        itemType = state.itemType
                    ),
                    quantity = lote.quantity.toIntOrNull() ?: 0,
                    expirationDate = lote.date
                )
            }

        uiState.value = state.copy(
            donationHelper = (state.donationHelper) + newEntries,
            itemName = "",
            itemBarCode = "",
            itemType = "",
            listDate = listOf(ExpirationDate())
        )
    }



    fun createDonation() {
        val state = uiState.value
        uiState.value = state.copy(isLoading = true)

        viewModelScope.launch {
            val donationHeader = DonationModel(
                date = state.donationDate,
                campaignId = state.campaignId,
                donorName = state.donorName
            )

            val result = createDonationServiceUseCase(
                donation = donationHeader,
                donationHelper = state.donationHelper
            )

            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isCreated = true,
                        donationHelper = emptyList()
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

    fun getCampaigns() {
        uiState.value = uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = getAllCampaignsUseCase()
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        campaigns = result.data
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

    fun onAddNewDate() {
        val currentList = uiState.value.listDate
        uiState.value = uiState.value.copy(
            listDate = currentList + ExpirationDate()
        )
    }

    fun updateExpiration(index: Int, value: String) {
        val updateList = uiState.value.listDate.toMutableList()
        if (index in updateList.indices) {
            updateList[index] = updateList[index].copy(date = value)
            uiState.value = uiState.value.copy(listDate = updateList)
        }
    }

    fun updateQuantity(index: Int, value: String) {
        val updateList = uiState.value.listDate.toMutableList()
        if (index in updateList.indices) {
            updateList[index] = updateList[index].copy(quantity = value)
            uiState.value = uiState.value.copy(listDate = updateList)
        }
    }
}