package com.ipca.socialstore.presentation.views.donation.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.donation.AddDonationService
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationState(
    val donation : DonationModel = DonationModel(donationDate = "", campaignId = null),
    val item : ItemModel = ItemModel(name = "", itemType = ""),
    val donationItem : DonationItemModel = DonationItemModel(itemId = 0, donationId = 0),
    val expirationDate: String = "",
    val quantity: Int = 0,
    val error : ErrorText? = null,
    val isLoading : Boolean? = null,
    val isCreated : Boolean? = false,
)
@HiltViewModel
class CreateDonationViewModel @Inject constructor(private val addDonationService: AddDonationService) : ViewModel(){

    val uiState = mutableStateOf(DonationState())


    //region Item
    fun updateItemName(name : String){
        val item = uiState.value.item.copy(
            name = name
        )
        uiState.value = uiState.value.copy(
            item = item
        )
    }

    fun updateItemType(type : String){
        val item = uiState.value.item.copy(
            itemType = type
        )
        uiState.value = uiState.value.copy(
            item = item
        )
    }

    //endregion

    //region Donations
    fun updateDonationDate(date : String){
        val donation = uiState.value.donation.copy(
            donationDate = date
        )
        uiState.value = uiState.value.copy(
            donation = donation
        )
    }

    //endregion

    //region Stock

    fun updateExpirationDate(date : String){
        uiState.value = uiState.value.copy(
            expirationDate = date
        )
    }

    fun updateQuantity(quantity : String){

        val newQuantity = quantity.toInt()
        uiState.value = uiState.value.copy(
            quantity = newQuantity
        )
    }
    //endregion

    //region DonationItem
    fun teste(){

    }
    //endregion

    fun addDonation(){

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
            isCreated = false
        )

        viewModelScope.launch {
            val result = addDonationService(
                item = uiState.value.item,
                donation = uiState.value.donation,
                expirationDate = uiState.value.expirationDate,
                quantity = uiState.value.quantity)
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
                        isCreated = false
                    )
                }
            }
        }
    }
}