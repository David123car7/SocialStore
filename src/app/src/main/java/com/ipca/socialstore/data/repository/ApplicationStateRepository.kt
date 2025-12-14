package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ApplicationStateRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createApplicationState(applicationState: ApplicationStateModel) : ResultWrapper<Int> {
        return try {
            val stateResult = supabaseClient.from(DatabaseTables.APPLICATION_STATE).insert(applicationState){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(stateResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(stateResult.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getApplicationState(id: Int): ResultWrapper<ApplicationStateModel>{
        return try {
            val applicationStateResult = supabaseClient.from(DatabaseTables.APPLICATION_STATE).select {
                filter {
                    eq("id", id)
                }
            }.decodeSingleOrNull<ApplicationStateModel>()
            if(applicationStateResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(applicationStateResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}