package com.ipca.socialstore.data.repository

import android.content.ClipData
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import com.ipca.socialstore.data.exceptions.ExceptionMapper


class ItemRepository @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun createItem(item : ItemModel) : ResultWrapper<Boolean> {
        return try {
            supabase.from(DatabaseTables.ITEM)
                .insert(item)
            ResultWrapper.Success(true)
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
}