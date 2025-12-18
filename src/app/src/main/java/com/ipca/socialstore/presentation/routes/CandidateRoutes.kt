package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class CandidateRoutes {
    @Serializable
    object  ApplicationState: CandidateRoutes()
}