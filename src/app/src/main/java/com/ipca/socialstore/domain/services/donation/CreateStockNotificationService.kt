package com.ipca.socialstore.domain.services.donation

import androidx.work.impl.utils.forName
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemByIdUseCase
import com.ipca.socialstore.domain.stock.GetStockById
import javax.inject.Inject

class CreateStockNotificationService @Inject constructor(
    private val exceptionMapper: ExceptionMapper,
    private val getStockById: GetStockById,
    private val getItemByIdUseCase: GetItemByIdUseCase
){

    suspend operator fun invoke(stockId : Int) : Any {
        return try {

            val getStock = getStockById(stockId)
            if(getStock is ResultWrapper.Error)
                return ResultWrapper.Error(getStock.error)
            val stock = (getStock as ResultWrapper.Success).data


            val getItem = getItemByIdUseCase()
        }catch (e : Exception){

        }
    }
}