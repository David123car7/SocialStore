package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class DonationRepository @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){
    suspend fun createDonation(donation : DonationModel) : ResultWrapper<Int> {
        return try {
            val donationResult = supabase.from(DatabaseTables.DONATION)
                .insert(donation){
                    select()
                }
                .decodeSingleOrNull<DonationModel>()
            if(donationResult == null) return ResultWrapper.Error(AppError.DataNotUpdated)
            if(donationResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(donationResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDonationById(id : Int) : ResultWrapper<DonationModel>{
        return try {
            val donationResult = supabase.from(DatabaseTables.DONATION)
                .select {
                    filter {
                        eq("id",id)
                    }
                }
                .decodeSingleOrNull<DonationModel>()
            if(donationResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(donationResult)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}