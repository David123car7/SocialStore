package com.ipca.socialstore.presentation.views.stock.List

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.ListAllItemsStockService
import com.ipca.socialstore.domain.stock.AddItemStockUseCase
import com.ipca.socialstore.domain.stock.RemoveStockUseCase
import com.ipca.socialstore.domain.stock.SetStockQuantityUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
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
    )

@HiltViewModel
class ListAllStockViewModel @Inject constructor(
    private val listAllItemsStockService: ListAllItemsStockService,
    private val removeStockUseCase: RemoveStockUseCase,
): ViewModel() {
    val uiState = mutableStateOf(GetStockState())

    init {
        getAllStock()
    }

    fun updateSearchList(name: String) {
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

    fun updateSearchListType(type: String) {
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

    fun getAllStock() {
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            listAllItemsStockService().collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {

                        val updatedList = result.data

                        val updatedSelected = updatedList.find {
                            it.stockId == uiState.value.selectedStock?.stockId
                        }

                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            isEditing = false,
                            items = result.data,
                            totalItems = result.data.size,
                            selectedStock = updatedSelected ?: uiState.value.selectedStock,
                        )
                    }

                    is ResultWrapper.Error -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            isEditing = false,
                            error = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun removeStock(stockId : String){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = removeStockUseCase(stockId.toInt())
                when (result) {
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
}
