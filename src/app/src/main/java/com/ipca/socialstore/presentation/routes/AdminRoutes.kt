package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable
@Serializable
sealed class AdminRoutes {
    @Serializable
    object Home : AdminRoutes()

    // --- Applications ---
    @Serializable
    object ListApplications: AdminRoutes()
    @Serializable
    object ApplicationState: AdminRoutes()

    // --- Campaigns ---
    @Serializable
    object CreateCampaign : AdminRoutes()
    @Serializable
    object CampaignEdit : AdminRoutes()
    @Serializable
    object CampaignList : AdminRoutes()

    // --- Items ---
    @Serializable
    object  CreateItem : AdminRoutes()

    @Serializable
    object GetSingleItem : AdminRoutes()

    // --- Donations ---
    @Serializable
    object CreateDonation : AdminRoutes()

    @Serializable
    object ListAllDonations : AdminRoutes()

    @Serializable
    object GetStock : AdminRoutes()

    @Serializable
    object StockDetails : AdminRoutes()

    @Serializable
    object CreationDonation: AdminRoutes()

    @Serializable
    object SelectStock : AdminRoutes()
    @Serializable
    object Reports : AdminRoutes()


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
    @Serializable
    object CreateDeliver : AdminRoutes()

}