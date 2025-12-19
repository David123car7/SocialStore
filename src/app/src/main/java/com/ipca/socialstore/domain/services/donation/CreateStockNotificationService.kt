package com.ipca.socialstore.domain.services.donation

import androidx.work.impl.utils.forName
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemByIdUseCase
import com.ipca.socialstore.domain.item.GetItemNameUseCase
import com.ipca.socialstore.domain.notificationSchedule.CreateInitialNotificationUseCase
import com.ipca.socialstore.domain.stock.GetStockById
import javax.inject.Inject

class CreateStockNotificationService @Inject constructor(
    private val exceptionMapper: ExceptionMapper,
    private val getStockById: GetStockById,
    private val getItemNameUseCase: GetItemNameUseCase,
    private val createInitialNotificationUseCase: CreateInitialNotificationUseCase
){

    suspend operator fun invoke(stockId : List<Int>) : ResultWrapper<Boolean> {
        return try {

            val getStock = getStockById(stockId)
            if(getStock is ResultWrapper.Error)
                return ResultWrapper.Error(getStock.error)
            val stock = (getStock as ResultWrapper.Success).data

            val itemsIds = stock.map { it.itemId }
            val itemResult = getItemNameUseCase(itemsIds)
            if(itemResult is ResultWrapper.Error)
                return ResultWrapper.Error(itemResult.error)
            val itemName = (itemResult as ResultWrapper.Success).data

            stock.zip(itemName).forEach { (stock, name) ->
                val notification = NotificationScheduledModel(
                    subject = "O $name tem validade até ${stock.expirationDate}, e existem ${stock.quantity} itens.",
                    isRead = false,
                    title = "O $name esta a ficar sem validade"
                )

                createInitialNotificationUseCase(notification)
            }
            return ResultWrapper.Success(true)

        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

}