package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentStateModel (
    val id: Int? = null,

    @SerialName("state")
    val state: String,

    @SerialName("description")
    val description: String,
)