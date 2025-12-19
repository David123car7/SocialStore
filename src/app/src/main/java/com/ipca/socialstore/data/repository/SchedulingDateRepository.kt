package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.SchedulingDateModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class SchedulingDateRepository @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper) {
    suspend fun createSchedulingDate(schedulingDate : SchedulingDateModel) : ResultWrapper<Int>{
        return try {
            val createResult = supabase.from(DatabaseTables.SCHEDULING_DATE)
                .insert(schedulingDate){
                    select(columns = Columns.list("id"))
                }.decodeSingleOrNull<TableIdModel>()
            if(createResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(createResult.id)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}