package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.NotificationAdminModel
import com.ipca.socialstore.data.models.NotificationBeneficiaryModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class NotificationAdminRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val exceptionMapper: ExceptionMapper
) {
    suspend fun createNotification(notification: NotificationAdminModel): ResultWrapper<Int> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_ADMIN).insert(notification){
                select(columns = Columns.list("id"))
            }.decodeSingle<TableIdModel>()
            ResultWrapper.Success(result.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getNotificationsUnsended(): ResultWrapper<List<NotificationAdminModel>> {
        return try {
            val result = supabase.from(DatabaseTables.NOTIFICATION_ADMIN).select {
                filter {
                    eq("sended", false)
                }
            }.decodeList<NotificationAdminModel>()
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun markNotificationAsSent(notificationId: Int): ResultWrapper<Unit> {
        return try {
            supabase.from(DatabaseTables.NOTIFICATION_ADMIN).update(
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