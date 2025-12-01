package com.ipca.socialstore.presentation.objects

import kotlinx.serialization.Serializable

sealed class Routes {

    // --- Authentication ---
    @Serializable
    object Login

    @Serializable
    object Register

    @Serializable
    object RequestResetPassword

    @Serializable
    object ResetPassword

    // --- Home Views ---
    @Serializable
    object DefaultHome

    @Serializable
    object BeneficiaryHome

    @Serializable
    object AdminHome

    // --- Campaigns ---
    @Serializable
    object CreateCampaign

    @Serializable
    object ListAllCampaign

    // --- Items ---
    @Serializable
    object  CreateItem

    @Serializable
    object GetSingleItem

    // --- Donations ---
    @Serializable
    object CreateDonation


}