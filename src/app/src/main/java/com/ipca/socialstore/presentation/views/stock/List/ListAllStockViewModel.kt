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
    val itemId : Int? = null,
    val quantity : String? = null,
    val date : String? = null,
    val stockId : String? = null,
    val itemName : String? = null,
    val itemType : String? = null,
    )

@HiltViewModel
class ListAllStockViewModel @Inject constructor(
    private val listAllItemsStockService: ListAllItemsStockService,
    private val setStockQuantityUseCase: SetStockQuantityUseCase,
    private val removeStockUseCase: RemoveStockUseCase,
    private val addItemStockUseCase: AddItemStockUseCase,
): ViewModel() {

    val uiState = mutableStateOf(GetStockState())


    //region Updates
    fun selectStock(item: StockReceiverModel) {
        uiState.value = uiState.value.copy(selectedStock = item)
    }


    fun updateQuantity(newValue: String, itemDate : String) {
        val value = newValue.toIntOrNull()
        uiState.value = uiState.value.copy(
            quantity =  newValue,
            date = itemDate
        )
    }

    fun updateDate(newDate: String) {
        uiState.value = uiState.value.copy(
            date = newDate
        )
    }

    fun updateItemId(newValue: String) {
        val value = newValue.toInt()
        uiState.value = uiState.value.copy(
            itemId = value
        )
        println(value)

    }

    fun updateStockId(value : String){
        uiState.value = uiState.value.copy(
            stockId = value
        )
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

    fun updateItemName(name : String){
        uiState.value = uiState.value.copy(
            itemName = name
        )
    }

    fun updateItemType(type : String){
        uiState.value = uiState.value.copy(
            itemType = type
        )
    }
    //endregion


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
                            quantity = null,
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

    fun saveStockChanges() {
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = setStockQuantityUseCase(itemId = uiState.value.itemId!!, quantity = uiState.value.quantity?.toInt()!!)
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

    fun addStock(itemId: Int){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            when (val result = addItemStockUseCase(itemId,uiState.value.date!!,uiState.value.quantity?.toInt()!!)) {
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
