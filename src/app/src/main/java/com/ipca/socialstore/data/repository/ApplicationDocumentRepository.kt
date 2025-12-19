package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ApplicationDocumentDocOnly
import com.ipca.socialstore.data.models.ApplicationDocumentModel
import com.ipca.socialstore.data.models.ApplicationDocumentStateOnly
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import javax.inject.Inject

class ApplicationDocumentRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createApplicationDocument(applicationDoc: ApplicationDocumentModel): ResultWrapper<Int>{
        return try {
            val applicationResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT).insert(applicationDoc){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(applicationResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(applicationResult.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun createApplicationDocuments(applicationDocList: List<ApplicationDocumentModel>): ResultWrapper<Unit> {
        return try{
            supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT).insert(applicationDocList)
            ResultWrapper.Success(Unit)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteApplicationDocument(id: Int): ResultWrapper<Unit> {
        return try {
            supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT).delete {
                filter {
                    eq("id", id)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteApplicationDocuments(ids: List<Int>): ResultWrapper<Unit> {
        return try {
            supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT).delete {
                filter {
                    isIn("id", ids)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    //counts the application documents related with one type of document
    suspend fun countApplicationDocumentsByType(appDocTypeId: Int): ResultWrapper<Int> {
        return try {
            val result = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT).select(
                columns = Columns.list("id"),
            ) {
                filter {
                    eq("app_doc_type_id", appDocTypeId)
                }
            }
            val total = result.countOrNull()?.toInt() ?: 0
            ResultWrapper.Success(total)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getApplicationDocuments(appDocTypeId: Int): ResultWrapper<List<ApplicationDocumentModel>>{
        return try{
            val applicationDocumentsResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT).select {
                filter {
                    eq("app_doc_type_id", appDocTypeId)
                }
            }.decodeList<ApplicationDocumentModel>()
            ResultWrapper.Success(applicationDocumentsResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getApplicationDocuments(appDocTypeIds: List<Int>): ResultWrapper<List<TableIdModel>>{
        return try{
            val applicationDocumentsResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT)
                .select(columns = Columns.list("id")) {
                    filter {
                        isIn("app_doc_type_id", appDocTypeIds)
                    }
                }.decodeList<TableIdModel>()
            ResultWrapper.Success(applicationDocumentsResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDocumentsIds(appDocTypeIds: List<Int>): ResultWrapper<List<ApplicationDocumentDocOnly>>{
        return try{
            val applicationDocumentsResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT)
                .select(columns = Columns.list("document_id")) {
                filter {
                    isIn("app_doc_type_id", appDocTypeIds)
                }
            }.decodeList<ApplicationDocumentDocOnly>()
            ResultWrapper.Success(applicationDocumentsResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDocumentStatesIds(appDocTypeIds: List<Int>): ResultWrapper<List<ApplicationDocumentStateOnly>>{
        return try{
            val applicationDocumentsResult = supabaseClient.from(DatabaseTables.APPLICATION_DOCUMENT)
                .select(columns = Columns.list("state_id")) {
                    filter {
                        isIn("app_doc_type_id", appDocTypeIds)
                    }
                }.decodeList<ApplicationDocumentStateOnly>()
            ResultWrapper.Success(applicationDocumentsResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}