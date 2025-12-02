package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper) {
    fun getUserSessionState(): Flow<ResultFlowWrapper<Boolean>> {
        return supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    ResultFlowWrapper.Success(true) as ResultFlowWrapper<Boolean>
                }
                is SessionStatus.NotAuthenticated -> {
                    ResultFlowWrapper.Success(false) as ResultFlowWrapper<Boolean>
                }
                is SessionStatus.Initializing -> {
                    ResultFlowWrapper.Loading(true)
                }
                else -> {
                    ResultFlowWrapper.Error(AppError.UnknownError("Unknown Auth State"))
                }
            }
        }.catch { e ->
            emit(ResultFlowWrapper.Error(exceptionMapper.map(e)))
        }
    }

    suspend fun login(email: String, password: String): ResultWrapper<Boolean> {
        return try {
            supabase.auth.signInWith(provider = Email){
                this.email = email
                this.password = password
            }
            ResultWrapper.Success(true)
        }
        catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun register(email: String, password: String): ResultWrapper<String> {
        return try {
            val user = supabase.auth.signUpWith(provider = Email){
                this.email = email
                this.password = password
            }
            ResultWrapper.Success(user?.id ?: "")
        }
        catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun signInWithToken(email: String, token: String): ResultWrapper<Boolean>{
        return try{
            supabase.auth.verifyEmailOtp(
                type = OtpType.Email.RECOVERY,
                email = email,
                token = token
            )
            ResultWrapper.Success(true)
        }
        catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun resetPassword(password: String): ResultWrapper<Boolean>{
        return try{
            supabase.auth.updateUser {
                this.password = password
            }
            ResultWrapper.Success(true)
        }
        catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun requestResetPassword(email : String) : ResultWrapper<Boolean>{
        return try{
            supabase.auth.resetPasswordForEmail(email = email)
            ResultWrapper.Success(true)
        }
        catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun logout(): ResultWrapper<Boolean>{
        return try{
            supabase.auth.signOut()
            ResultWrapper.Success(true)
        }
        catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

}