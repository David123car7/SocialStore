package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class ApplicationStateRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createApplicationState(applicationState: ApplicationStateModel) : ResultWrapper<Int> {
        return try {
            val state = supabaseClient.from(DatabaseTables.APPLICATION_STATE).insert(applicationState){
                select()
            }.decodeList<ApplicationStateModel>().firstOrNull()

            if(state == null)
                return ResultWrapper.Error(AppError.DataNotCreated)

            val id = state.id
                ?: return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))

            ResultWrapper.Success(state.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}