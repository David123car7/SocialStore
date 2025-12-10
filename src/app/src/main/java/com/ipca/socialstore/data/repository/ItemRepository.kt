package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import com.ipca.socialstore.data.exceptions.ExceptionMapper


class ItemRepository @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun createItem(item : ItemModel) : ResultWrapper<Int> {
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .insert(item){
                    select()
                }
                .decodeSingleOrNull<ItemModel>()
            if(itemResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            if(itemResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(itemResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemById(id : Int) : ResultWrapper<ItemModel>{
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .select {
                    filter {
                        eq("id",id)
                    }
                }
                .decodeSingleOrNull<ItemModel>()
            if(itemResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(itemResult)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemIdByName(name : String) : ResultWrapper<Int>{
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .select {
                    filter {
                        eq("name",name)
                    }
                }
                .decodeSingleOrNull<ItemModel>()
            if(itemResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            if(itemResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(itemResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemIdList(listId: List<Int>): ResultWrapper<List<ItemModel>> {
        return try {
            if (listId.isEmpty()) return ResultWrapper.Success(emptyList())
            val items = supabase.from(DatabaseTables.ITEM)
                .select {
                    filter {
                        isIn("id", listId) // ⚡ The Magic Operator
                    }
                }
                .decodeList<ItemModel>()
            ResultWrapper.Success(items)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}