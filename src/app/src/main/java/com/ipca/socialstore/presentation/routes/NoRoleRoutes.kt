package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class NoRoleRoutes {
    @Serializable
    object DefaultHome: NoRoleRoutes()

    @Serializable
    object Application: NoRoleRoutes()
}