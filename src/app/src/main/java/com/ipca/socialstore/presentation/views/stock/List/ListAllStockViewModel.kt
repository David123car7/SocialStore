package com.ipca.socialstore.presentation.views.stock.List

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.presentation.models.StockReveiverModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.stock.ListAllItemsStockService
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GetStockState(
    val stock : List<StockModel>? = null,
    val items : List<StockReveiverModel>? = null,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
    val isEditing : Boolean? = false,

    )

@HiltViewModel
class ListAllStockViewModel @Inject constructor(private val listAllItemsStockService: ListAllItemsStockService): ViewModel(){

    val uiState = mutableStateOf(GetStockState())

    private val stockDetail = mutableStateOf<StockReveiverModel?>(null)
    val detail : State<StockReveiverModel?> = stockDetail

    fun selectStock(item : StockReveiverModel){
        stockDetail.value = item
        println("FuncaoModel:${stockDetail.value}")
    }
    fun getAllStock(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = listAllItemsStockService()
            when (result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isEditing = false,
                        items = result.data
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
