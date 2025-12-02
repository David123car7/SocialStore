package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.ItemModelCreation


class ItemRepository @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun createItem(item : ItemModelCreation) : ResultWrapper<ItemModel> {
        return try {
            val result = supabase.from(DatabaseTables.ITEM)
                .insert(item){
                    select()
                }
                .decodeSingle<ItemModel>()
            ResultWrapper.Success(result)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }


    suspend fun getItem(itemId : String) : ResultWrapper<ItemModel>{
        return try {
            val item = supabase.from(DatabaseTables.ITEM)
                .select {
                    filter {
                        eq("item_id",itemId)
                    }
                }
                .decodeSingle<ItemModel>()
            ResultWrapper.Success(data = item)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemByName(itemName : String) : ResultWrapper<ItemModel>{
        return try {
            val item = supabase.from(DatabaseTables.ITEM)
                .select {
                    filter {
                        eq("name",itemName)
                    }
                }
                .decodeSingle<ItemModel>()
            ResultWrapper.Success(data = item)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}