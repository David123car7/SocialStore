package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
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

class UserRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun getUserRole(): ResultWrapper<UserRole> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return ResultWrapper.Error(AppError.UserNotLoggedIn)

            @Serializable
            data class RoleContainer(
                @SerialName("role") val role: UserRole
            )

            val userProfile = supabase.from(DatabaseTables.PROFILE)
                .select (columns = Columns.list("role")) {
                    filter { eq("uid", userId) }
                }
                .decodeSingleOrNull<RoleContainer>()

            if (userProfile != null) {
                ResultWrapper.Success(userProfile.role)
            } else {
                ResultWrapper.Error(AppError.UserNotFound)
            }
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun registerUser(user: UserModel): ResultWrapper<Boolean>{
        return try {
            supabase.from(DatabaseTables.PROFILE).insert(user)
            ResultWrapper.Success(true)
        }
        catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}