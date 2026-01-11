package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper) {

    suspend fun createInitialNotification(notification : NotificationScheduledModel) : ResultWrapper<Boolean>{
        return try {
            supabase.from(DatabaseTables.NOTIFICATION_SCHEDULED)
                .insert(notification)
            ResultWrapper.Success(true)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllNotifications(): ResultWrapper<List<NotificationScheduledModel>>{
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_SCHEDULED)
                .select()
                .decodeList<NotificationScheduledModel>()
            ResultWrapper.Success(result)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun checkNotificationExists(uniqueKey: String): ResultWrapper<NotificationScheduledModel> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_SCHEDULED)
                .select {
                    filter {
                        eq("notification_key", uniqueKey)
                    }
                }
                .decodeSingleOrNull<NotificationScheduledModel>()

            if (result == null){
                ResultWrapper.Error(AppError.DataNotFound)
            }
            else{
                ResultWrapper.Success(result )
            }

        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}