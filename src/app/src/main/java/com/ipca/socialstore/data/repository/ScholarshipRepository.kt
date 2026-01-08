package com.ipca.socialstore.data.repository

import android.util.Log
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ScholarshipModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.data.room.entitys.toEntity
import com.ipca.socialstore.data.room.entitys.toModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ScholarshipRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper
) {
    suspend fun createScholarship(scholarship: ScholarshipModel): ResultWrapper<Int> {
        return try {
            val academicResult = supabaseClient.from(DatabaseTables.SCHOLARSHIP).insert(scholarship){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(academicResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(academicResult.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getScholarship(id: Int): ResultWrapper<ScholarshipModel>{
        return try {
            val academicResult = supabaseClient.from(DatabaseTables.SCHOLARSHIP).select {
                filter {
                    eq("id", id)
                }
            }.decodeSingle<ScholarshipModel>()
            ResultWrapper.Success(academicResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteScholarship(id: Int): ResultWrapper<Unit> {
        return try {
            supabaseClient.from(DatabaseTables.SCHOLARSHIP).delete {
                filter {
                    eq("id", id)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getScholarshipList(ids: List<Int>): ResultWrapper<List<ScholarshipModel>>{
        return try {
            val applicationDataStateResult = supabaseClient.from(DatabaseTables.SCHOLARSHIP).select {
                filter {
                    isIn("id", ids)
                }
            }.decodeList<ScholarshipModel>()
            ResultWrapper.Success(applicationDataStateResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}