package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class GeneralRoutes {
    @Serializable
    object Login : GeneralRoutes()

    @Serializable
    object Register : GeneralRoutes()


    @Serializable
    object RequestResetPassword : GeneralRoutes()

    @Serializable
    object ResetPassword : GeneralRoutes()
}