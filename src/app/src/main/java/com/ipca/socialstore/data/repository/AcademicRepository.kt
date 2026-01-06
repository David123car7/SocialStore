package com.ipca.socialstore.data.repository

import android.util.Log
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.data.room.entitys.toEntity
import com.ipca.socialstore.data.room.entitys.toModel
import com.ipca.socialstore.data.room.interfaces.AcademicInterface
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class AcademicRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper,
    private val academicInterface: AcademicInterface) {

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

    suspend fun updateAcademicData(academicModel: AcademicModel): ResultWrapper<Int> {
        if(academicModel.id == null)
            return ResultWrapper.Error(AppError.UnknownError("Academic Id Null"))

        return try {
            val academicResult = supabaseClient.from(DatabaseTables.ACADEMIC).update(academicModel) {
                filter {
                    eq("id", academicModel.id)
                }
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if (academicResult == null) {
                return ResultWrapper.Error(AppError.DataNotFound)
            }
            academicInterface.insertAcademic(academicModel.toEntity())
            ResultWrapper.Success(academicModel.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }


    suspend fun deleteAcademic(id: Int): ResultWrapper<Unit> {
        return try {
            supabaseClient.from(DatabaseTables.ACADEMIC).delete {
                filter {
                    eq("id", id)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAcademic(id: Int): ResultWrapper<AcademicModel>{
        val localData = academicInterface.getAcademic(id)

        if (localData != null) {
            Log.d("App Debug", "There is local data")
            return ResultWrapper.Success(localData.toModel())
        }

        return try {
            val academicResult = supabaseClient.from(DatabaseTables.ACADEMIC).select {
                filter {
                    eq("id", id)
                }
            }.decodeSingleOrNull<AcademicModel>()
            if(academicResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            academicInterface.insertAcademic(academicResult.toEntity())
            ResultWrapper.Success(academicResult)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}