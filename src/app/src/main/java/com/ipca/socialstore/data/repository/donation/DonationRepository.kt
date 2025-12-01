package com.ipca.socialstore.data.repository.donation

import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.objects.DatabaseTables
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class DonationRepository @Inject constructor(private val supabase : SupabaseClient){

    suspend fun createDonation(donation : DonationModel) : ResultWrapper<Boolean>{
        return try {
            supabase.from(DatabaseTables.donation)
                .insert(donation)
            ResultWrapper.Success(true)
        }catch (e : Exception){
            ResultWrapper.Error(e.message ?: "Unknown Error")
        }

    }
}