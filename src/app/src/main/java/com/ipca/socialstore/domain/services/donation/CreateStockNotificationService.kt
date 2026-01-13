package com.ipca.socialstore.domain.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ipca.socialstore.R // Importa os teus recursos
import com.ipca.socialstore.data.enums.NotificationTypes
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.NotificationAdminModel
import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.NotificationAdminRepository
import com.ipca.socialstore.data.repository.NotificationRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemNameUseCase
import com.ipca.socialstore.domain.notificationAdmin.CreateAdminNotificationUseCase
import com.ipca.socialstore.domain.notificationSchedule.CreateInitialNotificationUseCase
import com.ipca.socialstore.domain.stock.GetStockById

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreateStockNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exceptionMapper: ExceptionMapper,
    private val getStockById: GetStockById,
    private val getItemNameUseCase: GetItemNameUseCase,
    private val createInitialNotificationUseCase: CreateInitialNotificationUseCase,
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val createAdminNotificationUseCase: CreateAdminNotificationUseCase,
) {
    private val CHANNEL_ID = "STOCK_ALERTS_CHANNEL"

    suspend operator fun invoke(stockId: List<Int>): ResultWrapper<Boolean> {
        return try {
            val isAdmin = checkIfUserIsAdmin()

            if (!isAdmin) {
                android.util.Log.d("AUTH", "Utilizador não autorizado a processar notificações de stock.")
                return ResultWrapper.Success(false)
            }
            val getStock = getStockById(stockId)
            if (getStock is ResultWrapper.Error)
                return ResultWrapper.Error(getStock.error)

            val stockList = (getStock as ResultWrapper.Success).data

            val itemsIds = stockList.map { it.itemId }
            val itemResult = getItemNameUseCase(itemsIds)
            if (itemResult is ResultWrapper.Error)
                return ResultWrapper.Error(itemResult.error)

            val itemNames = (itemResult as ResultWrapper.Success).data

            stockList.zip(itemNames).forEach { (stockItem, name) ->
                val uniqueKey = "${stockItem.id}_${stockItem.expirationDate}"

                val notificationTitle = "Validade próxima: $name"
                val notificationSubject = "O item $name expira dia ${stockItem.expirationDate}."

                val checkResult = notificationRepository.checkNotificationExists(uniqueKey)

                if (checkResult is ResultWrapper.Error && checkResult.error is AppError.DataNotFound) {
                    val notificationModel = NotificationScheduledModel(
                        subject = notificationSubject,
                        isRead = false,
                        title = notificationTitle,
                        notificationKey = uniqueKey
                    )
                    createInitialNotificationUseCase(notificationModel)
                }

                createAdminNotificationUseCase(
                    tittle = notificationTitle,
                    description = notificationSubject,
                    type = NotificationTypes.STOCK.type
                )
            }
            ResultWrapper.Success(true)

        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

     suspend fun checkIfUserIsAdmin(): Boolean {
        return try {
            val userResult = userRepository.getUserRole()

            if (userResult is ResultWrapper.Success) {
                userResult.data.value == "admin"
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}