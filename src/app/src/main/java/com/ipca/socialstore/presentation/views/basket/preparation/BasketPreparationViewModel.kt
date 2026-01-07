package com.ipca.socialstore.presentation.views.basket.preparation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.beneficiary.GetBeneficiaryByIdUseCase
import com.ipca.socialstore.domain.deliveryItems.GetDeliveryItemsBySchedulingIdUseCase
import com.ipca.socialstore.domain.services.CreateDeliveryServiceUseCase
import com.ipca.socialstore.domain.services.ListAllItemsStockService
import com.ipca.socialstore.domain.services.scheduling.GetAllInfoBeneficiaryUseCase
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


data class BasketPreparationState(
    val beneficiary : BeneficiaryModel? = null,
    val isLoading : Boolean ?= false,
    val error : ErrorText? = null,
    val scheduling : List<SchedulingModel>? = null,
    val acceptScheduling : List<SchedulingModel>? = null,
    val listStock : List<StockReceiverModel>? = emptyList(),
    val selectedCategory: String = "Tudo",
    val selectedQuantities: Map<Int, Int> = emptyMap(),
    val searchResult : List<StockReceiverModel>? = null,
    val filteredList : List<StockReceiverModel>?= null,
    val selectScheduling : SchedulingModel? = null,
)

@HiltViewModel
class BasketPreparationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBeneficiaryByIdUseCase: GetBeneficiaryByIdUseCase,
    private val getAllInfoBeneficiaryUseCase: GetAllInfoBeneficiaryUseCase,
    private val listAllItemsStockService: ListAllItemsStockService,
    private val createDeliveryServiceUseCase: CreateDeliveryServiceUseCase,
    private val getDeliveryItemsBySchedulingIdUseCase: GetDeliveryItemsBySchedulingIdUseCase

): ViewModel()
{
    val uiState = mutableStateOf(BasketPreparationState())
    private val beneficiaryId: String? = savedStateHandle["beneficiaryId"]
    private var allStockItems: List<StockReceiverModel> = emptyList()

    init {
        fetchBeneficiary()
        getAllStock()
    }

    private fun fetchBeneficiary() {
        val id = beneficiaryId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = ErrorText.DynamicString("Beneficário não encontrado"))
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getBeneficiaryByIdUseCase(id)
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        beneficiary = result.data,
                        isLoading = false
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        error = result.error.asUiText(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun fetchExistingItems() {
        val sId = uiState.value.selectScheduling?.id ?: return
        uiState.value = uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = createDeliveryServiceUseCase.getItemsByScheduling(sId)

            uiState.value = uiState.value.copy(
                isLoading = false,
                selectedQuantities = if (result is ResultWrapper.Success) {
                    result.data.associate { it.stockId to it.quantity }
                } else {
                    emptyMap()
                }
            )
        }
    }
    fun fetchInfo(){
        val id = beneficiaryId?.toIntOrNull()

        if (id == null) {
            uiState.value = uiState.value.copy(error = ErrorText.DynamicString("Beneficário não encontrado"))
            return
        }
        uiState.value = uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = getAllInfoBeneficiaryUseCase(id)
            if (result is ResultWrapper.Success) {
                uiState.value = uiState.value.copy(
                    scheduling = result.data.scheduling,
                )
            }
        }
    }


    fun getAllStock() {
        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            listAllItemsStockService().collect { result ->
                when (result) {
                    is ResultWrapper.Success -> {
                        allStockItems = result.data.filter { it.totalQuantity > 0 }
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = null,
                            filteredList = allStockItems
                        )
                    }
                    is ResultWrapper.Error -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                            error = result.error.asUiText()
                        )
                    }
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        uiState.value = uiState.value.copy(selectedCategory = category)

        val filtered = if (category == "Tudo") {
            allStockItems
        }
        else {
            allStockItems.filter { it.item.itemType == category }
        }
        uiState.value = uiState.value.copy(filteredList = filtered)
    }

    fun updateItemQuantity(stockId: Int, operation: Int) {

        val currentQuantities = uiState.value.selectedQuantities.toMutableMap()

        val currentSelectedQty = currentQuantities[stockId] ?: 0

        val newQty = (currentSelectedQty + operation).coerceAtLeast(0)


        val stockItem = allStockItems.find { it.stockId == stockId }
        val maxAvailable = stockItem?.totalQuantity ?: 0

        if (newQty <= maxAvailable) {
            currentQuantities[stockId] = newQty

            uiState.value = uiState.value.copy(
                selectedQuantities = currentQuantities
            )
        }
    }

    fun updateSearchList(name: String) {
        if (name.isEmpty()) {
            uiState.value = uiState.value.copy(
                filteredList = allStockItems
            )
            return
        }

        val normalizedSearch = name.trim().lowercase()

        val results = allStockItems.filter { item ->
            item.item.name.lowercase().contains(normalizedSearch)
        }

        uiState.value = uiState.value.copy(
            filteredList = results
        )
    }

    fun updateSchedulingId(scheduling : SchedulingModel) {

        uiState.value = uiState.value.copy(
            selectScheduling = scheduling,
            selectedQuantities = emptyMap()
        )
        println(scheduling)
        fetchExistingItems()
    }

    fun createDeliveryService() {
        val sId = uiState.value.selectScheduling?.id
        println(sId)

        if (sId == null) {
            uiState.value = uiState.value.copy(
                error = ErrorText.DynamicString("Erro: Agendamento não selecionado")
            )
            return
        }

        uiState.value = uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val result = createDeliveryServiceUseCase(
                schedulingId = sId,
                stockMap = uiState.value.selectedQuantities
            )

            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }


}