package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ApplicationDocumentTypeRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createApplicationDocumentType(applicationDocState: ApplicationDocumentTypeModel): ResultWrapper<Int>{
        return try {
            val applicationResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT_TYPE).insert(applicationDocState){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(applicationResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(applicationResult.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteApplicationDocumentType(id: Int): ResultWrapper<Unit> {
        return try {
            supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT_TYPE).delete {
                filter {
                    eq("id", id)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getApplicationDocumentType(applicationId: Int): ResultWrapper<List<ApplicationDocumentTypeModel>>{
        return try{
            val appDocTypeResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT_TYPE).select {
                filter {
                    eq("application_id", applicationId)
                }
            }.decodeList<ApplicationDocumentTypeModel>()
            return ResultWrapper.Success(appDocTypeResult)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getApplicationDocumentTypeId(applicationId: Int, documentType: String): ResultWrapper<Int>{
        return try{
            val appDocTypeResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT_TYPE).select {
                filter {
                    eq("application_id", applicationId)
                    eq("type", documentType)
                }
            }.decodeSingleOrNull<ApplicationDocumentTypeModel>()
            if(appDocTypeResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            if(appDocTypeResult.id == null) return ResultWrapper.Error(AppError.DataNotFound)
            return ResultWrapper.Success(appDocTypeResult.id)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}