package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable
@Serializable
sealed class AdminRoutes {
    @Serializable
    object AdminHome : AdminRoutes()

    @Serializable
    object ListApplications: AdminRoutes()

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

    @Serializable
    object GetStock : AdminRoutes()

    @Serializable
    object StockDetails : AdminRoutes()

    @Serializable
    object CreationDonation: AdminRoutes()

    @Serializable
    object SelectStock : AdminRoutes()

    @Serializable
    object CreateScheduling : AdminRoutes()

    @Serializable
    object NotificationHistory : AdminRoutes()

    @Serializable
    object SchedulingMainPage : AdminRoutes()

    @Serializable
    object SchedulingManagement : AdminRoutes()

    @Serializable
    object BeneficiaryManagement : AdminRoutes()
}