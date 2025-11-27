package com.ipca.socialstore.data.helpers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

///For more than one document
fun Query.snapshotFlow(): Flow<QuerySnapshot> = callbackFlow {
    val listenerRegistration = addSnapshotListener { value, error ->
        if (error != null) {
            close()
            return@addSnapshotListener
        }
        if (value != null)
            trySend(value)
    }
    awaitClose {
        listenerRegistration.remove()
    }
}

//For only one document
fun DocumentReference.snapshotFlow(): Flow<DocumentSnapshot> = callbackFlow {
    val listenerRegistration = addSnapshotListener { value, error ->
        if (error != null) {
            close()
            return@addSnapshotListener
        }
        if (value != null)
            trySend(value)
    }
    awaitClose {
        listenerRegistration.remove()
    }
}

fun FirebaseAuth.authStateFlow(): Flow<Boolean> = callbackFlow {
    val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser != null)
    }

    addAuthStateListener(authStateListener)

    awaitClose {
        removeAuthStateListener(authStateListener)
    }
}

suspend fun FirebaseAuth.reloadUserSession(): Boolean {
    return try {
        val user = this.currentUser ?: return false
        user.reload().await()
        true
    } catch (e: Exception) {
        false
    }
}