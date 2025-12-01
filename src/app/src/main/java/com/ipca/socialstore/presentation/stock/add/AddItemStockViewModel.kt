package com.ipca.socialstore.presentation.stock.add

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.campaign.GetAllCampaignsUseCase
import com.ipca.socialstore.domain.stock.AddItemStockUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StockState(
    val item : StockModel = StockModel("","",0),
    val isLoading : Boolean = false,
    val error: ErrorText? = null,
)

@HiltViewModel
class AddItemStockViewModel @Inject constructor(private val addItemStockUseCase: AddItemStockUseCase): ViewModel(){

    val uiState = mutableStateOf(StockState())

    fun addItemStock(){

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null,
        )

        viewModelScope.launch {
            val result = addItemStockUseCase(uiState.value.item)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false
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