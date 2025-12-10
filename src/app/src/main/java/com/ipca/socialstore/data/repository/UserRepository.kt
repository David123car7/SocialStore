package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.models.UserModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
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

            val user = supabase.from(DatabaseTables.USER)
                .select (columns = Columns.list("role")) {
                    filter { eq("uid", userId) }
                }
                .decodeSingleOrNull<RoleContainer>()

            if (user != null) {
                ResultWrapper.Success(user.role)
            } else {
                ResultWrapper.Error(AppError.UserNotFound)
            }
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun createUser(user: UserModel): ResultWrapper<Int>{
        return try {
            val userResult = supabase.from(DatabaseTables.USER).insert(user){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(userResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(userResult.id)
        }
        catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getUserApplicationId(uid: String): ResultWrapper<Int>{
        return try {
            val user = getUser(uid = uid) ?: return ResultWrapper.Error(AppError.UserNotFound)
            user.applicationId ?: return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(user.applicationId)
        }
        catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun setUserApplicationId(uid: String, id: Int): ResultWrapper<String>{
        return try {
            val user = getUser(uid = uid) ?: return ResultWrapper.Error(AppError.UserNotFound)

            val newUser = user.copy(applicationId = id)
            supabase.from(DatabaseTables.USER).update(newUser) {
                filter { eq("uid", uid) }
            }

            ResultWrapper.Success(uid)
        }
        catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    //Must be private
    private suspend fun getUser(uid: String): UserModel?{
        val user = supabase.from(DatabaseTables.USER).select(columns = Columns.list()){
            filter { eq("uid", uid) }
        }.decodeList<UserModel>().firstOrNull()
        return user
    }
}