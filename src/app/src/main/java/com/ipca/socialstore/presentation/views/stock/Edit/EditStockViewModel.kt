package com.ipca.socialstore.presentation.views.stock.Edit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.stock.SetStockQuantityUseCase
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditState(
    val stock: StockReceiverModel? = null,
    val quantity: String? = null,
    val error: ErrorText? = null,
    val isEditing : Boolean? = false,
    val isLoading: Boolean?= false
)

@HiltViewModel
class EditStockViewModel @Inject constructor( private val setStockQuantityUseCase: SetStockQuantityUseCase): ViewModel(){

    val uiState = mutableStateOf(EditState())

    fun updateQuantity(value : String){
        uiState.value = uiState.value.copy(
            quantity = value
        )
    }

    fun getStockId(stockId : Int){
        val stock = uiState.value.stock?.copy(
            stockId = stockId
        )
        uiState.value = uiState.value.copy(
            stock = stock
        )
    }

    fun updateEditState(){
        uiState.value = uiState.value.copy(
            isEditing = true
        )
    }

    fun updateItemInStock(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            isEditing = false,
            error = null
        )

        viewModelScope.launch {
            val result = setStockQuantityUseCase(uiState.value.stock?.stockId!!,uiState.value.quantity?.toIntOrNull()!!)
            when (result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
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