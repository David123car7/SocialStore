package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ApplicationDataStateRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createApplicationDataState(applicationDataState: ApplicationDataStateModel) : ResultWrapper<Int> {
        return try {
            val dataStateResult = supabaseClient.from(DatabaseTables.APPLICATION_DATA_STATE).insert(applicationDataState){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(dataStateResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(dataStateResult.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateApplicationDataState(applicationDataState: ApplicationDataStateModel): ResultWrapper<Int> {
        if(applicationDataState.id == null)
            return ResultWrapper.Error(AppError.UnknownError("Appllication Data State Id Null"))

        return try {
            supabaseClient.from(DatabaseTables.APPLICATION_DATA_STATE).update(applicationDataState) {
                filter {
                    eq("id", applicationDataState.id)
                }
            }
            ResultWrapper.Success(applicationDataState.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getApplicationState(id: Int): ResultWrapper<ApplicationDataStateModel>{
        return try {
            val applicationDataStateResult = supabaseClient.from(DatabaseTables.APPLICATION_DATA_STATE).select {
                filter {
                    eq("id", id)
                }
            }.decodeSingleOrNull<ApplicationDataStateModel>()
            if(applicationDataStateResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(applicationDataStateResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getApplicationDataStates(ids: List<Int>): ResultWrapper<List<ApplicationDataStateModel>>{
        return try {
            val applicationDataStateResult = supabaseClient.from(DatabaseTables.APPLICATION_DATA_STATE).select {
                filter {
                    isIn("id", ids)
                }
            }.decodeList<ApplicationDataStateModel>()
            ResultWrapper.Success(applicationDataStateResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}