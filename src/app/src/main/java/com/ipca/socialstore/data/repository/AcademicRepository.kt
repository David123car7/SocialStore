package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class AcademicRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper) {

    suspend fun createAcademic(academicModel: AcademicModel): ResultWrapper<Int> {
        return try {
            val state = supabaseClient.from(DatabaseTables.ACADEMIC).insert(academicModel){
                select()
            }.decodeSingleOrNull<AcademicModel>()
            if(state == null) return ResultWrapper.Error(AppError.DataNotCreated)
            if(state.id == null) return ResultWrapper.Error(AppError.UnknownError(""))
            ResultWrapper.Success(state.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}