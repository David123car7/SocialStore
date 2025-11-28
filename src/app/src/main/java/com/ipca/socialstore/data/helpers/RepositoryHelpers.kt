package com.ipca.socialstore.data.helpers
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

fun FirebaseAuth.authStateFlow(): Flow<Boolean> = callbackFlow {
    val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser != null)
    }

    addAuthStateListener(authStateListener)

    awaitClose {
        removeAuthStateListener(authStateListener)
    }
}

fun iogurtePessego

suspend fun FirebaseAuth.reloadUserSession(): Boolean {
    return try {
        val user = this.currentUser ?: return false
        user.reload().await()
        true
    } catch (e: Exception) {
        false
    }
}