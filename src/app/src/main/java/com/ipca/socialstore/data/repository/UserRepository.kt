package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.objects.DatabaseTables
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class UserRepository @Inject constructor(private val supabase: SupabaseClient){

    suspend fun RegisterUser(user: UserModel): ResultWrapper<Boolean>{
        return try {
            supabase.from(DatabaseTables.profile).insert(user)
            ResultWrapper.Success(true)
        }
        catch (e: RestException){
            ResultWrapper.Error(e.error)
        }
        catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Unknown Error")
        }
    }
}