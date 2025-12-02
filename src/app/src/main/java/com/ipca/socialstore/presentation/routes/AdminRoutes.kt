package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable
@Serializable
sealed class AdminRoutes {
    @Serializable
    object AdminHome : AdminRoutes()

    // --- Campaigns ---
    @Serializable
    object CreateCampaign : AdminRoutes()

    @Serializable
    object ListAllCampaign : AdminRoutes()

    // --- Items ---
    @Serializable
    object  CreateItem : AdminRoutes()

    @Serializable
    object GetSingleItem : AdminRoutes()

    // --- Donations ---
    @Serializable
    object CreateDonation : AdminRoutes()
}