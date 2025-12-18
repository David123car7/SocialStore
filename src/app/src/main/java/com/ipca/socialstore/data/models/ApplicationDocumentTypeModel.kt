package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDocumentTypeModel(
    val id: Int? = null,

    @SerialName("application_id")
    val applicationId: Int,

    @SerialName("type")
    val type: String,

    @SerialName("state")
    val state: String,

    @SerialName("description")
    val description: String?,
)