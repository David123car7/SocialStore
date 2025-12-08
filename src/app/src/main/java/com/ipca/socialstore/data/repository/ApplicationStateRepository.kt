package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.views.application.ApplicationState
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class ApplicationStateRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createApplicationState(applicationState: ApplicationStateModel) : ResultWrapper<Int?> {
        return try {
            val state = supabaseClient.from(DatabaseTables.APPLICATION_STATE).insert(applicationState){
                select()
            }.decodeSingle<ApplicationStateModel>()

            ResultWrapper.Success(state.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}