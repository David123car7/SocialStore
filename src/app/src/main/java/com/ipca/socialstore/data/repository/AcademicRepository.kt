package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class AcademicRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper) {

    suspend fun createAcademic(academicModel: AcademicModel): ResultWrapper<Int> {
        return try {
            val academicResult = supabaseClient.from(DatabaseTables.ACADEMIC).insert(academicModel){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(academicResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(academicResult.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAcademic(id: Int): ResultWrapper<AcademicModel>{
        return try {
            val academicResult = supabaseClient.from(DatabaseTables.ACADEMIC).select {
                filter {
                    eq("id", id)
                }
            }.decodeSingleOrNull<AcademicModel>()
            if(academicResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(academicResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}