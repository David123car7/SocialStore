package com.ipca.socialstore.domain.services.stock

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.stock.GetStockByItemIdUseCase
import com.ipca.socialstore.domain.stock.UpdateQuantityInStockByDateUseCase
import javax.inject.Inject

class CreateItemStockDonationService @Inject constructor(
    private val getStockByItemIdUseCase: GetStockByItemIdUseCase,
    private val updateQuantityInStockByDateUseCase: UpdateQuantityInStockByDateUseCase,
    private val stockRepository: StockRepository,
    private val exceptionMapper: ExceptionMapper
) {
    suspend operator fun invoke(itemId: Int, expirationDate: String, quantity: Int): ResultWrapper<Boolean> {
        return try {
            val dateInDbFormat = formatToDbDate(expirationDate)

            val stockResult = getStockByItemIdUseCase(itemId, listOf(dateInDbFormat))

            val existingStock = when (stockResult) {
                is ResultWrapper.Success -> stockResult.data.find { it.expirationDate == dateInDbFormat }
                is ResultWrapper.Error -> {
                    if (stockResult.error == AppError.DataNotFound) null
                    else return ResultWrapper.Error(stockResult.error)
                }
            }

            // 2. Se existe, soma. Se não existe, cria.
            if (existingStock != null) {
                updateQuantityInStockByDateUseCase(existingStock.id!!, quantity)
            } else {
                val newStock = StockModel(
                    itemId = itemId,
                    expirationDate = dateInDbFormat,
                    quantity = quantity
                )
                stockRepository.addStock(newStock)
            }

            ResultWrapper.Success(true)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    private fun formatToDbDate(uiDate: String): String {
        return try {
            val normalizedDate = uiDate.replace("/", "-")
            val parts = normalizedDate.split("-")
            if (parts.size == 3 && parts[0].length != 4) {
                "${parts[2]}-${parts[1]}-${parts[0]}"
            } else normalizedDate
        } catch (e: Exception) { uiDate }
    }
}