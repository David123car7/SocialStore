package com.ipca.socialstore.presentation.views.stock.List

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.ListAllItemsStockService
import com.ipca.socialstore.domain.stock.SetStockQuantityUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GetStockState(
    val stock : List<StockModel>? = null,
    val items : List<StockReceiverModel>? = null,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val isEditing : Boolean? = false,
    val selectedStock : StockReceiverModel? = null,
    val searchResult : List<StockReceiverModel>? = null,
    val totalItems : Int? = null,
    val itemId : Int? = null,
    val quantity : Int? = null

    )

@HiltViewModel
class ListAllStockViewModel @Inject constructor(private val listAllItemsStockService: ListAllItemsStockService, private val setStockQuantityUseCase: SetStockQuantityUseCase): ViewModel(){

    val uiState = mutableStateOf(GetStockState())

    fun selectStock(item : StockReceiverModel){
        uiState.value = uiState.value.copy(selectedStock = item)
    }

    fun updateSearchList(name : String){
        val allItems = uiState.value.items

        if (name.isEmpty() || allItems.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                searchResult = null,
            )
            return
        }
        val normalizedSearch = name.trim().lowercase()

        val filteredList = allItems.filter { item ->
            item.item.name.lowercase().contains(normalizedSearch)
        }

        uiState.value = uiState.value.copy(
            searchResult = filteredList,
        )
    }

    fun updateSearchListType(type : String){
        val allItems = uiState.value.items

        if (type.isEmpty() || allItems.isNullOrEmpty()) {
            uiState.value = uiState.value.copy(
                searchResult = null,
            )
            return
        }
        val normalizedSearch = type.trim().lowercase()

        val filteredList = allItems.filter { item ->
            item.item.itemType.lowercase().contains(normalizedSearch)
        }

        uiState.value = uiState.value.copy(
            searchResult = filteredList,
        )
    }


    fun updateQuantity(date : String,newDate : String, quantity: Int){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = setStockQuantityUseCase(uiState.value.itemId!!, uiState.value.quantity!!)
            when (result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isEditing = false,

                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isEditing = false,
                        error = result.error.asUiText(),
                    )
                }
            }
        }
    }
    fun getAllStock(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = listAllItemsStockService()
            when (result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isEditing = false,
                        items = result.data,
                        totalItems = result.data.count()

                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isEditing = false,
                        error = result.error.asUiText(),
                    )
                }
            }
        }
    }
}
