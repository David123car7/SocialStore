package com.ipca.socialstore.data.repository

import androidx.compose.runtime.MutableState
import com.google.firebase.auth.FirebaseAuth
import com.ipca.socialstore.presentation.login.LoginState
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) {
    fun getCurrentUserUID(): String? {
        return auth.currentUser?.uid
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
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
}