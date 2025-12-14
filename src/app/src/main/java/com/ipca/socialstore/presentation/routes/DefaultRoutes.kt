package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class DefaultRoutes {
    @Serializable
    object CreateApplication: DefaultRoutes()

    @Serializable
    object Application: DefaultRoutes()

    @Serializable
    object DefaultHome: DefaultRoutes()
}