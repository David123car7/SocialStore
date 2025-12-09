package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentModel (
    @SerialName("path")
    val path: String,

    @SerialName("status")
    val status: String,

    @SerialName("application_id")
    val applicationId: Int,
)