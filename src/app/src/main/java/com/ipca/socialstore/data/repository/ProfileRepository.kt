package com.ipca.socialstore.data.repository

import com.ipca.socialstore.R
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ProfileModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createProfile(profile: ProfileModel): ResultWrapper<Int>{
        return try {
            val profile = supabase.from(DatabaseTables.PROFILE).insert(profile){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(profile == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(profile.id)
        }
        catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}
