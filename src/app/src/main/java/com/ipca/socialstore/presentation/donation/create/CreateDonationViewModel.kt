package com.ipca.socialstore.presentation.donation.create

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.ItemModelCreation
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donation.AddItemToDonationUseCase
import com.ipca.socialstore.domain.donation.CreateDonationUseCase
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.item.GetItemByNameUseCase
import com.ipca.socialstore.domain.item.GetItemUseCase
import com.ipca.socialstore.domain.logic.AddDonationLogicUseCase
import com.ipca.socialstore.domain.stock.AddItemStockUseCase
import com.ipca.socialstore.domain.stock.GetItemByIdStock
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationState(
    val donation : DonationModel = DonationModel("","",""),
    val item : ItemModelCreation = ItemModelCreation("",""),
    val stock : StockModel = StockModel("","",0),
    val donationItem : DonationItemModel = DonationItemModel("",""),
    val quantity: Int? = 0,
    val error : ErrorText? = null,
    val isLoading : Boolean? = null,
    val isCreated : Boolean? = false,
)
@HiltViewModel
class CreateDonationViewModel @Inject constructor(private val addDonationLogicUseCase: AddDonationLogicUseCase) : ViewModel(){

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
        val stock = uiState.value.stock.copy(
            expirationDate = date
        )
        uiState.value = uiState.value.copy(
            stock = stock
        )
    }

    fun updateQuantity(quantity : String){

        val newQuantity = quantity.toIntOrNull()
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
            val item = uiState.value.item
            val donation = uiState.value.donation
            val stock = uiState.value.stock
            val quantity = uiState.value.quantity ?: 0
            val result = addDonationLogicUseCase(item, stock, quantity,donation)
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