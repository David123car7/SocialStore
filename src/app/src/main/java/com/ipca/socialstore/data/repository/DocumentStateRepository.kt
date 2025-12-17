package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DocumentStateModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class DocumentStateRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper){
    suspend fun createDocumentState(documentState: DocumentStateModel): ResultWrapper<Int> {
        return try{
            val documentResult = supabase.from(DatabaseTables.DOCUMENT_STATE).insert(documentState){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(documentResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(documentResult.id)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    //does this make sense?
    suspend fun createDocumentsStates(documentStatesList: List<DocumentStateModel>): ResultWrapper<Unit> {
        return try{
            supabase.from(DatabaseTables.DOCUMENT_STATE).insert(documentStatesList)
            ResultWrapper.Success(Unit)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteDocumentState(id: Int): ResultWrapper<Unit> {
        return try {
            supabase.from(DatabaseTables.DOCUMENT_STATE).delete {
                filter {
                    eq("id", id)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDocumentState(id: Int): ResultWrapper<DocumentStateModel>{
        return try{
            val documentResult = supabase.from(DatabaseTables.DOCUMENT_STATE).select(){
                filter {
                    eq("id",id)
                }
            }.decodeSingleOrNull<DocumentStateModel>()
            if(documentResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(documentResult)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}