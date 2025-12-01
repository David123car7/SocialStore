package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import javax.inject.Inject

class DonationRepository @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun createDonation(donation : DonationModel) : ResultWrapper<Boolean> {
        return try {
            supabase.from(DatabaseTables.DONATION)
                .insert(donation)
            ResultWrapper.Success(true)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}