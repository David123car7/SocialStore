package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class SchedulingRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createScheduling(scheduling : SchedulingModel) : ResultWrapper<Int> {
        return try {
            val scheduling = supabase.from(DatabaseTables.SCHEDULING)
                .insert(scheduling) {
                    select(columns = Columns.list("id"))
                }.decodeAsOrNull<TableIdModel>()
            if (scheduling == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(scheduling.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}