package com.ipca.socialstore.presentation.views.item

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.services.stock.CreateItemStockService
import com.ipca.socialstore.domain.stock.CreateItemStockUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ExpirationDate(
    val date: String = "",
    val quantity: String = ""
)
data class ItemState(
    val item : ItemModel = ItemModel(name = "", itemType = ""),
    val listDate : List<ExpirationDate> = listOf(ExpirationDate()),
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val isCreated : Boolean  = false,
    val quantity : String? = null,
    val date: String? = null
)
@HiltViewModel
class CreateItemViewModel @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val createItemStockUseCase: CreateItemStockUseCase,
    private val createItemStockService: CreateItemStockService
): ViewModel(){

    val uiState = mutableStateOf(ItemState())


    fun updateQuantity(quantity: String){

        uiState.value = uiState.value.copy(
            quantity = quantity
        )
    }

    fun updateDate(date : String){
        uiState.value = uiState.value.copy(
            date = date
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
    fun addNewDate() {
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

}
