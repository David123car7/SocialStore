package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScholarshipModel (
    val id: Int? = null,

    @SerialName("value")
    val value: Float,
)
