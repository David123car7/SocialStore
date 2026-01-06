package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class BeneficiaryRoutes {
    @Serializable
    object Home: BeneficiaryRoutes()

    @Serializable
    object Scheduling : BeneficiaryRoutes()


    @Serializable
    object Documents : BeneficiaryRoutes()

    @Serializable
    object Profile : BeneficiaryRoutes()

    @Serializable
    object EditProfile : BeneficiaryRoutes()


    @Serializable
    object JustifyScheduling : BeneficiaryRoutes()

    @Serializable
    object SchedulingConfirmation : BeneficiaryRoutes()
}