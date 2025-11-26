package com.ipca.socialstore.data.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.ipca.socialstore.data.helpers.authStateFlow
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) {
     fun getUserSessionState(): Flow<ResultFlowWrapper<Boolean>> {
        return auth.authStateFlow()
            .map { isLoggedIn ->
                ResultFlowWrapper.Success(isLoggedIn) as ResultFlowWrapper<Boolean>
            }
            .onStart {
                emit(ResultFlowWrapper.Loading<Boolean>())
            }
            .catch { exception ->
                emit(ResultFlowWrapper.Error(exception.message ?: ""))
            }
     }

    suspend fun login(email: String, password: String): ResultWrapper<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            ResultWrapper.Success(true)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "")
        }
    }

    suspend fun loginWithMicrosoft(activity: Activity): Result<Boolean> {
        return try {
            // 1. Configure the provider
            val provider = OAuthProvider.newBuilder("microsoft.com")
            // Optional: Request specific scopes (like reading calendar)
            // provider.addCustomParameter("prompt", "login")

            // 2. Start the flow
            // We use .await() to pause until the user finishes logging in
            val result = auth.startActivityForSignInWithProvider(activity, provider.build()).await()

            // 3. Success
            if (result.user != null) {
                Result.success(true)
            } else {
                Result.failure(Exception("Microsoft login failed: User is null"))
            }

        } catch (e: Exception) {
            // 4. Catch errors (User closed window, Network error, etc.)
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): ResultWrapper<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email,password).await()
            ResultWrapper.Success(true)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "")
        }
    }

    fun logout(): Result<Boolean>{
        return try{
            auth.signOut()
            Result.success(true)
        }
        catch (e: Exception){
            Result.failure(e)
        }
    }
}