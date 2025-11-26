package com.ipca.socialstore.data.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.ipca.socialstore.Models.DonationModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DonationsRepository @Inject constructor(private val db : FirebaseFirestore) {

    fun addDonation(donation : DonationModel) : ResultWrapper<Boolean>{
        try {
            val donationsCollection = db.collection("Doações")
            donationsCollection.document().set(donation)
            return ResultWrapper.Success(true)
        }catch (e: Exception){

            Log.e("Erros", e.message ?: "Erro desconhecido")
            return ResultWrapper.Error(e.message?: "Erro ")


        }
    }
}