package com.ipca.socialstore.presentation.item

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ItemState(
    val item : ItemModel = ItemModel("",""),
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val isCreated : Boolean  = false
)
@HiltViewModel
class CreateItemViewModel @Inject constructor(private val createItemUseCase: CreateItemUseCase): ViewModel(){

    val uiState = mutableStateOf(ItemState())


    fun updateItemName(itemName : String){
        val itemName = uiState.value.item.copy(
            name = itemName
        )
        uiState.value = uiState.value.copy(
            item = itemName
        )
    }

    fun updateItemType(itemType : String){
        val itemType = uiState.value.item.copy(
            itemType = itemType
        )
        uiState.value = uiState.value.copy(
            item = itemType
        )
    }


    fun createItem(item : ItemModel){

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
            isCreated = false
        )

        viewModelScope.launch {
            val result = createItemUseCase(uiState.value.item)
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
