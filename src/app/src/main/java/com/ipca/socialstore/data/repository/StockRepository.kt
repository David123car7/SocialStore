package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class StockRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper ){

    suspend fun addItemStock(item : StockModel) : ResultWrapper<Boolean> {
        return try {
            supabase.from(DatabaseTables.STOCK)
                .insert(item)
            ResultWrapper.Success(true)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}