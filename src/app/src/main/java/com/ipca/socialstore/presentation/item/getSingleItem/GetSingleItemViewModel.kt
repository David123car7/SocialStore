package com.ipca.socialstore.presentation.item.getSingleItem

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GetItemState(
    val item : ItemModel? = ItemModel("","",""),
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val itemId : String? = null
)
@HiltViewModel
class GetSingleItemViewModel @Inject constructor(private val getItemUseCase: GetItemUseCase): ViewModel(){

    val uiState = mutableStateOf(GetItemState())

    fun updateSearchId(itemId : String){
        uiState.value = uiState.value.copy(
            itemId = itemId
        )
    }


    fun getSingleItem(){

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
        )

        viewModelScope.launch {
            val result = getItemUseCase(uiState.value.itemId ?: "")
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
                    )
                }
            }
        }
    }
}