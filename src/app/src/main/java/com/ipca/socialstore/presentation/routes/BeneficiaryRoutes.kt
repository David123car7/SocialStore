package com.ipca.socialstore.presentation.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class BeneficiaryRoutes {
    @Serializable
    object BeneficiaryHome: BeneficiaryRoutes()
}