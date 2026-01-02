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

    suspend fun updateApplicationState(applicationState: ApplicationStateModel): ResultWrapper<Int> {
        if(applicationState.id == null)
            return ResultWrapper.Error(AppError.UnknownError("Appllication State Id Null"))

        return try {
            supabaseClient.from(DatabaseTables.APPLICATION_STATE).update(applicationState) {
                filter {
                    eq("id", applicationState.id!!)
                }
            }
            ResultWrapper.Success(applicationState.id)
        } catch (e: Exception) {
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

    suspend fun getApplicationStates(ids: List<Int>): ResultWrapper<List<ApplicationStateModel>>{
        return try {
            val applicationStateResult = supabaseClient.from(DatabaseTables.APPLICATION_STATE).select {
                filter {
                    isIn("id", ids)
                }
            }.decodeList<ApplicationStateModel>()
            ResultWrapper.Success(applicationStateResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun setApplicationStateMessage(id: Int, message: String): ResultWrapper<Int> {
        return try {
            val updateData = mapOf("message" to message)
            supabaseClient.from(DatabaseTables.APPLICATION_STATE).update(updateData) {
                filter {
                    eq("id", id)
                }
            }
            ResultWrapper.Success(id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteApplicationState(id: Int): ResultWrapper<Unit> {
        return try {
            supabaseClient.from(DatabaseTables.APPLICATION_STATE).delete {
                filter {
                    eq("id", id)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}