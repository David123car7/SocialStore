package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentModel (
    val id: Int? = null,

    @SerialName("path")
    val path: String,

    @SerialName("name")
    val name: String,

    @SerialName("folder_name")
    val folderName: String,

    @SerialName("status")
    val status: String,

    @SerialName("application_id")
    val applicationId: Int,
)