package com.ipca.socialstore.presentation.views.item

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.item.GetAllItemsUseCase
import com.ipca.socialstore.domain.item.GetItemByCodeUseCase
import com.ipca.socialstore.domain.services.stock.CreateItemStockService
import com.ipca.socialstore.domain.stock.CreateItemStockUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ExpirationDate(
    val date: String = "",
    val quantity: String = ""
)
data class ItemState(
    val item : ItemModel = ItemModel(name = "", itemType = "", barCode = ""),
    val listDate : List<ExpirationDate> = listOf(ExpirationDate()),
    val barCodeNotFound: Boolean = false,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val isCreated : Boolean  = false,
    val quantity : String? = null,
    val date: String? = null,
    val itemType: String? = null,
    val items : List<ItemModel>? = emptyList(),
)
@HiltViewModel
class CreateItemViewModel @Inject constructor(
    private val createItemStockService: CreateItemStockService,
    private val getItemByCodeUseCase: GetItemByCodeUseCase,
    private val getAllItemsUseCase: GetAllItemsUseCase
): ViewModel(){

    val uiState = mutableStateOf(ItemState())
    fun updateItemName(itemName : String){
        val itemName = uiState.value.item.copy(
            name = itemName
        )
        uiState.value = uiState.value.copy(
            item = itemName
        )
    }

    fun updateItemType(itemType : String) {
        val itemType = uiState.value.item.copy(
            itemType = itemType
        )
        uiState.value = uiState.value.copy(
            item = itemType
        )
    }

    fun updateBarCode(code : String) {
        val item = uiState.value.item.copy(
            barCode = code
        )
        uiState.value = uiState.value.copy(
            item = item
        )
    }

    fun removeFields(index: Int) {
        val newList = uiState.value.listDate.toMutableList()

        if (index in newList.indices) {
            newList.removeAt(index)
        }

        uiState.value = uiState.value.copy(
            listDate = newList
        )
    }

    fun addNewFields() {
        val newList = uiState.value.listDate + ExpirationDate()
        uiState.value = uiState.value.copy(listDate = newList)
    }
    fun updateMapDate(index: Int, date: String? = null, qty: String? = null) {
        val aux = uiState.value.listDate.toMutableList()
        val updatedEntry = aux[index].copy(
            date = date ?: aux[index].date,
            quantity = qty ?: aux[index].quantity
        )
        aux[index] = updatedEntry
        uiState.value = uiState.value.copy(listDate = aux)
    }

    fun getItemByCode(barCode: String){
        viewModelScope.launch {
            val result = getItemByCodeUseCase(barCode = barCode)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        item = result.data
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText(),
                        isCreated = false,
                        barCodeNotFound = true
                    )
                    updateBarCode(code = barCode)
                }
            }
        }
    }
    fun createItem(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
            isCreated = false
        )

        viewModelScope.launch {
            val result = createItemStockService(item = uiState.value.item,uiState.value.listDate)
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

    fun getItems(){
        viewModelScope.launch {
            val result = getAllItemsUseCase()
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        items = result.data
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText(),
                    )
                }
            }
        }
    }

    fun filterItem(filter: String) {
        if (filter.isBlank()) {
            uiState.value = uiState.value.copy(
                item = uiState.value.item.copy(
                    name = "",
                    itemType = "",
                    barCode = ""
                )
            )
            return
        }

        val allItems = uiState.value.items

        val foundItem = allItems?.find {
            it.name.equals(filter, ignoreCase = true) || it.barCode == filter
        }

        if (foundItem != null) {
            uiState.value = uiState.value.copy(
                item = foundItem
            )
        } else {
            uiState.value = uiState.value.copy(
                item = uiState.value.item.copy(
                    name = filter,
                    itemType = ""
                )
            )
        }
    }

}
