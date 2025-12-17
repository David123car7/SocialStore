package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class DocumentRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper){
    suspend fun createDocument(document: DocumentModel): ResultWrapper<Int> {
        return try{
            val documentResult = supabase.from(DatabaseTables.DOCUMENT).insert(document){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(documentResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(documentResult.id)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun createDocuments(documentsList: List<DocumentModel>): ResultWrapper<Unit> {
        return try{
            supabase.from(DatabaseTables.DOCUMENT).insert(documentsList)
            ResultWrapper.Success(Unit)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteDocument(documentId: Int): ResultWrapper<Unit> {
        return try {
            supabase.from(DatabaseTables.DOCUMENT).delete {
                filter {
                    eq("id", documentId)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDocuments(applicationId: Int, folderName: String): ResultWrapper<List<DocumentModel>>{
        return try{
            val documentResult = supabase.from(DatabaseTables.DOCUMENT).select(){
                filter {
                    eq("application_id",applicationId)
                    eq("folder_name", folderName)
                }
            }.decodeList<DocumentModel>()
            ResultWrapper.Success(documentResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllDocuments(applicationId: Int) : ResultWrapper<List<DocumentModel>>{
        return try{
            val documentResult = supabase.from(DatabaseTables.DOCUMENT).select(){
                filter {
                    eq("application_id",applicationId)
                }
            }.decodeList<DocumentModel>()
            ResultWrapper.Success(documentResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    private suspend fun getDocument(id: Int): ResultWrapper<DocumentModel> {
        return try{
            val documentResult = supabase.from(DatabaseTables.DOCUMENT).select(){
                filter {
                    eq("id",id)
                }
            }.decodeSingleOrNull<DocumentModel>()
            if(documentResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(documentResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}