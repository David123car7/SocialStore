package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class DonationItemRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun addItemDonation(donationItem : DonationItemModel) : ResultWrapper<Int> {
        return try {
            val itemDonationResult = supabase.from(DatabaseTables.DONATION_ITEM)
                .insert(donationItem){
                    select()
                }
                .decodeSingleOrNull<DonationItemModel>()
            if(itemDonationResult == null) return ResultWrapper.Error(AppError.DataNotUpdated)
            if(itemDonationResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(itemDonationResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}