package com.ipca.socialstore.presentation.stock.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.ItemModelCreation
import com.ipca.socialstore.data.models.StockHelper
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemUseCase
import com.ipca.socialstore.domain.logic.ListAllItemsStockUseCase
import com.ipca.socialstore.domain.stock.GetFullStockUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GetStockState(
    val stock : List<StockModel>? = null,
    val items : List<StockHelper>? = null,
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
)

@HiltViewModel
class ListAllStockViewModel @Inject constructor(private val listAllItemsStockUseCase: ListAllItemsStockUseCase): ViewModel(){

    val uiState = mutableStateOf(GetStockState())
    fun getAllStock(){
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = listAllItemsStockUseCase()
            println(result.data)
            when (result){
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
}
