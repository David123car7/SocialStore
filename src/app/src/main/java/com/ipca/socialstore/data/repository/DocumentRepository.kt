package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class DocumentRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper){
    suspend fun createDocument(document: DocumentModel): ResultWrapper<Boolean> {
        return try{
            supabase.from(DatabaseTables.DOCUMENT).insert(document)
            ResultWrapper.Success(true)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDocument(document: DocumentModel): ResultWrapper<DocumentModel> {
        return try{
            val document = supabase.from(DatabaseTables.DOCUMENT).insert(document){
                select()
            }.decodeAsOrNull<DocumentModel>()

            if(document == null)
                return ResultWrapper.Error(AppError.UnknownError("Get Document Error"))

            ResultWrapper.Success(document)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }}