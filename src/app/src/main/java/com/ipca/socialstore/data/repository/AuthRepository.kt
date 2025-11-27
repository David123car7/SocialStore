package com.ipca.socialstore.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.ipca.socialstore.data.helpers.authStateFlow
import com.ipca.socialstore.data.helpers.reloadUserSession
import com.ipca.socialstore.data.resultwrappers.ResultFlowWrapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth: FirebaseAuth) {
    fun getUserSessionState(): Flow<ResultFlowWrapper<Boolean>> {
        return auth.authStateFlow()
            .map { isLoggedIn ->
                ResultFlowWrapper.Loading(false) as ResultFlowWrapper<Boolean>
                if(isLoggedIn){
                    val validSession = auth.reloadUserSession()
                    if(validSession)
                        ResultFlowWrapper.Success(true) as ResultFlowWrapper<Boolean>
                    else{
                        logout()
                        ResultFlowWrapper.Success(false) as ResultFlowWrapper<Boolean>
                    }
                }
                else
                    ResultFlowWrapper.Success(false) as ResultFlowWrapper<Boolean>
            }
            .onStart {
                emit(ResultFlowWrapper.Loading(true))
            }
            .catch { exception ->
                logout()
                emit(ResultFlowWrapper.Error(exception.message ?: "GetUserSessionState Error"))
            }
    }

    suspend fun login(email: String, password: String): ResultWrapper<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            ResultWrapper.Success(true)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Login Error")
        }
    }

    suspend fun register(email: String, password: String): ResultWrapper<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email,password).await()
            ResultWrapper.Success(true)
        } catch (e: Exception) {
            ResultWrapper.Error(e.message ?: "Register Error")
        }
    }

    fun logout(): ResultWrapper<Boolean>{
        return try{
            auth.signOut()
            ResultWrapper.Success(true)
        }
        catch (e: Exception){
            ResultWrapper.Error(e.message ?: "Logout error")
        }
    }
}