package com.ipca.socialstore.data.repository

import android.util.Log
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.NotificationAdminModel
import com.ipca.socialstore.data.models.NotificationUserModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class NotificationBeneficiaryRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val exceptionMapper: ExceptionMapper
) {
    suspend fun createNotification(notification: NotificationUserModel): ResultWrapper<Int> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_BENEFICIARY).insert(notification){
                select(columns = Columns.list("id"))
            }.decodeSingle<TableIdModel>()
            ResultWrapper.Success(result.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getNotificationsUnread(uid: String): ResultWrapper<List<NotificationReceiverModel>> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_BENEFICIARY).select {
                filter {
                    eq("read", false)
                    eq("user_id", uid)
                }
            }.decodeList<NotificationReceiverModel>()
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getNotificationsUnread(limit: Int): ResultWrapper<List<NotificationReceiverModel>> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_BENEFICIARY).select {
                filter {
                    eq("read", false)
                }
                limit(limit.toLong())
            }.decodeList<NotificationReceiverModel>()
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllNotifications(): ResultWrapper<List<NotificationReceiverModel>> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_BENEFICIARY).select {
            }.decodeList<NotificationReceiverModel>()
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllNotifications(limit: Int): ResultWrapper<List<NotificationReceiverModel>> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_BENEFICIARY).select {
                limit(limit.toLong())
            }.decodeList<NotificationReceiverModel>()
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun markNotificationAsSent(notificationId: Int): ResultWrapper<Unit> {
        return try {
            supabase.from(DatabaseTables.NOTIFICATION_BENEFICIARY).update(
                {
                    set("sended", true)
                }
            ) {
                filter {
                    eq("id", notificationId)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}