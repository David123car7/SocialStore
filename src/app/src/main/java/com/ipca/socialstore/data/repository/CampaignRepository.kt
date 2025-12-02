package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class CampaignRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun createCampaign(campaign : CampaignModel) : ResultWrapper<Boolean> {
        return try {
            supabase.from(DatabaseTables.CAMPAIGN)
                .insert(campaign)
            ResultWrapper.Success(true)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllCampaign() : ResultWrapper<List<CampaignModel>> {
        return try {
            val campaigns = supabase.from(DatabaseTables.CAMPAIGN)
                .select()
                .decodeList<CampaignModel>()
             ResultWrapper.Success(campaigns)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}