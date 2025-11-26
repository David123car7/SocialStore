package com.ipca.socialstore.data.repository

import android.app.Activity
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.ipca.socialstore.presentation.login.LoginState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) {
    fun getUserSessionState(): Result<Boolean> {
        val uid = auth.currentUser?.uid
        return if(uid == null)
            Result.success(false)
        else
            Result.success(true)
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
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

    fun register(email: String, password: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email,password)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
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