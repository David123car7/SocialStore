package com.ipca.socialstore.presentation.views.stock.detail

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.stock.GetStockByIdService
import com.ipca.socialstore.domain.services.stock.UpdateStockService
import com.ipca.socialstore.domain.stock.AddStockUseCase
import com.ipca.socialstore.domain.stock.RemoveStockByDateUseCase
import com.ipca.socialstore.domain.stock.RemoveStockUseCase
import com.ipca.socialstore.domain.stock.SetStockQuantityUseCase
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.models.StockReceiverModel2
import com.ipca.socialstore.presentation.models.initializeStockReceiverModel2
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



data class StockItemDetailState(
    val stock : StockReceiverModel2 = initializeStockReceiverModel2(),
    val isLoading : Boolean = false,
    val error : ErrorText? = null,
    )

@HiltViewModel
class StockItemDetailViewModel @Inject constructor(
    private val getStockByIdService: GetStockByIdService,
    private val addStockUseCase: AddStockUseCase,
    private val updateStockService: UpdateStockService,
    private val removeStockByDateUseCase: RemoveStockByDateUseCase,
    savedStateHandle: SavedStateHandle
    ) : ViewModel() {
    val uiState = mutableStateOf(StockItemDetailState())

    val itemId: String? = savedStateHandle["item_id"]

    init {
        if(!itemId.isNullOrEmpty())
            getStock(itemId.toInt())
    }

    fun updateItemName(newName: String) {
        uiState.value = uiState.value.copy(
            stock = uiState.value.stock.copy(
                item = uiState.value.stock.item.copy(name = newName)
            )
        )
    }

    fun updateItemType(newType: String) {
        uiState.value = uiState.value.copy(
            stock = uiState.value.stock.copy(
                item = uiState.value.stock.item.copy(itemType = newType)
            )
        )
    }

    fun updateItemBarcode(newBarcode: String) {
        uiState.value = uiState.value.copy(
            stock = uiState.value.stock.copy(
                item = uiState.value.stock.item.copy(barCode = newBarcode)
            )
        )
    }

    fun updateBatchQuantity(dateKey: String, newQuantityStr: String) {
        val newQuantity = newQuantityStr.toIntOrNull() ?: 0

        val currentStock = uiState.value.stock

        val newQuantityMapList = currentStock.quantityMap.map { map ->
            if (map.containsKey(dateKey)) {
                // Create a new map with the updated value for this key
                map + (dateKey to newQuantity)
            } else {
                map
            }
        }

        val newTotal = newQuantityMapList.sumOf { map -> map.values.sum() }

        uiState.value = uiState.value.copy(
            stock = currentStock.copy(
                quantityMap = newQuantityMapList,
                totalQuantity = newTotal
            )
        )
    }

    fun updateStock(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = updateStockService(uiState.value.stock.item, uiState.value.stock.quantityMap)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
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

    fun removeStock(date: String){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = removeStockByDateUseCase(itemId = uiState.value.stock.item.id!!, date = date)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    getStock(itemId = uiState.value.stock.item.id!!)
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

    fun addStock(date: String, qnt: Int){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val stock = StockModel(itemId = uiState.value.stock.item.id!!, expirationDate = date, quantity = qnt)
            val result = addStockUseCase(stock)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    getStock(itemId = uiState.value.stock.item.id!!)
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

    fun getStock(itemId: Int){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = getStockByIdService(itemId = itemId)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        stock = result.data
                    )
                    println(result.data)
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
}

fun initializeStockReceiverModel(): StockReceiverModel {
    return StockReceiverModel(
        item = ItemModel(
            id = -1,
            name = "",
            barCode = "",
            itemType = ""
        ),
        stockId = -1,
        totalQuantity = -1,
        quantityMap = emptyMap()
    )
}