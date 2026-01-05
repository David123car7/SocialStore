package com.ipca.socialstore.presentation.views.item.getSingleItem

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemByIdUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GetItemState(
    val item : ItemModel? = null,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val itemId : Int = 0
)
@HiltViewModel
class GetSingleItemViewModel @Inject constructor(private val getItemByIdUseCase: GetItemByIdUseCase): ViewModel(){

    val uiState = mutableStateOf(GetItemState())

    fun updateSearchId(value : String){
        val newValue = value.toIntOrNull() ?: return

        uiState.value = uiState.value.copy(
            itemId = newValue
        )
    }


    fun getSingleItem(){

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
        )

        viewModelScope.launch {
            val result = getItemByIdUseCase(uiState.value.itemId)
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