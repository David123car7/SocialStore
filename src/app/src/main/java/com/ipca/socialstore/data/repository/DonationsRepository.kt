package com.ipca.socialstore.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DonationsRepository @Inject constructor(private val db : FirebaseFirestore) {

    suspend fun addDonation(donation : DonationModel) : ResultWrapper<Boolean> {
        try {
            val donationsCollection = db.collection("donations")
            donationsCollection.document().set(donation).await()
            return ResultWrapper.Success(true)
        }catch (e: Exception){

            Log.e("Erros", e.message ?: "Erro desconhecido")
            return ResultWrapper.Error(e.message?: "Erro ")
        }
    }
}