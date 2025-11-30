package com.ipca.socialstore.data.repository.campaign

import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.objects.DatabaseTables
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class CampaignRepository @Inject constructor(private val supabase: SupabaseClient){

    suspend fun createCampaign(campaign : CampaignModel) : ResultWrapper<Boolean>{
        return try {
            supabase.from(DatabaseTables.campaign)
                .insert(campaign)
            ResultWrapper.Success(true)
        }catch (e : Exception){
            ResultWrapper.Error(e.message ?: "Unknown Error")
        }
    }

    suspend fun getAllCampaign() : ResultWrapper<List<CampaignModel>>{
        return try {
            val campaigns = supabase.from(DatabaseTables.campaign)
                .select()
                .decodeList<CampaignModel>()
             ResultWrapper.Success(campaigns)
        }catch (e : Exception){
            ResultWrapper.Error(e.message ?: "Unknown Error")
        }
    }
}