package com.ipca.socialstore.presentation.views.stock.edit

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemByIdUseCase
import com.ipca.socialstore.domain.item.UpdateItemUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditItemState(
    val item: ItemModel = ItemModel(name = "", itemType = "", barCode = ""),
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val isEdited: Boolean = false
)

@HiltViewModel
class EditItemViewModel @Inject constructor(
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val uiState = mutableStateOf(EditItemState())

    val itemId: String? = savedStateHandle["item_id"]

    init {
        if(itemId != null)
            getItem(id = itemId.toInt())
    }

    fun updateName(name: String) {
        uiState.value = uiState.value.copy(
            item = uiState.value.item.copy(name = name)
        )
    }

    fun updateItemType(type: String) {
        uiState.value = uiState.value.copy(
            item = uiState.value.item.copy(itemType = type)
        )
    }

    fun updateBarCode(code: String) {
        uiState.value = uiState.value.copy(
            item = uiState.value.item.copy(barCode = code)
        )
    }

    fun getItem(id: Int){
        Log.d("App Debug", "${id}")
        viewModelScope.launch {
            val result = getItemByIdUseCase(itemId = id)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        item = result.data,
                        isEdited = true
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

    fun updateItem(){
        viewModelScope.launch {
            val result = updateItemUseCase(item = uiState.value.item)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isEdited = true
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
}