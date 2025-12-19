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

    @SerialName("created_at")
    val createdAt: String,
)

@Serializable
data class DocumentPathOnlyModel(val path: String)