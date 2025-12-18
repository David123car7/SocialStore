package com.ipca.socialstore.presentation.views.stock.List

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.stock.ListAllItemsStockService
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
    val searchResult : List<StockReceiverModel>? = null
    )

@HiltViewModel
class ListAllStockViewModel @Inject constructor(private val listAllItemsStockService: ListAllItemsStockService): ViewModel(){

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
                        items = result.data
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
