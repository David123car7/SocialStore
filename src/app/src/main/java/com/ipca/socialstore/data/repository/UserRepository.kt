package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class UserRepository @Inject constructor(private val supabase: SupabaseClient){

    suspend fun getUserRole(): ResultWrapper<UserRole> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
            if(userId == null) return ResultWrapper.Error("User not logged in")

            @Serializable
            data class RoleContainer(
                @SerialName("role") val role: UserRole //
            )

            val userProfile = supabase.from(DatabaseTables.PROFILE)
                .select (columns = Columns.list("role")) {
                    filter { eq("uid", userId) }
                }
                .decodeSingleOrNull<RoleContainer>()

            if (userProfile != null) {
                ResultWrapper.Success(userProfile.role)
            } else {
                ResultWrapper.Error("Profile not found")
            }
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Error")
        }
    }

    suspend fun registerUser(user: UserModel): ResultWrapper<Boolean>{
        return try {
            supabase.from(DatabaseTables.PROFILE).insert(user)
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