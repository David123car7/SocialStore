package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.objects.DatabaseTables
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(private val supabase: SupabaseClient){

    suspend fun getUserRole(): ResultWrapper<String> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
            if(userId == null) return ResultWrapper.Error("User not logged in")

            val userProfile = supabase.from(DatabaseTables.profile)
                .select (columns = Columns.list("role")) {
                    filter { eq("uid", userId) }
                }
                .decodeSingleOrNull<String>()

            if (userProfile != null) {
                ResultWrapper.Success(userProfile)
            } else {
                ResultWrapper.Error("Profile not found")
            }
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Error")
        }
    }

    suspend fun registerUser(user: UserModel): ResultWrapper<Boolean>{
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