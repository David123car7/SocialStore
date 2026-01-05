package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class CampaignRepository @Inject constructor(
    private val supabase: SupabaseClient,
    val exceptionMapper: ExceptionMapper){
    suspend fun createCampaign(campaign : CampaignModel) : ResultWrapper<Int> {
        return try {
            val campaingResult = supabase.from(DatabaseTables.CAMPAIGN)
                .insert(campaign){
                    select(columns = Columns.list("id"))
                }.decodeSingleOrNull<TableIdModel>()
            if(campaingResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(campaingResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllCampaigns() : ResultWrapper<List<CampaignModel>> {
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