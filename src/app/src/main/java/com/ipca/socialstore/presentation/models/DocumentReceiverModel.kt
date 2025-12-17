package com.ipca.socialstore.presentation.models

import kotlinx.serialization.SerialName

class DocumentReceiverModel(
    val id: Int? = null,

    @SerialName("path")
    val path: String,

    @SerialName("name")
    val name: String,

    @SerialName("folder_name")
    val folderName: String,

    @SerialName("created_at")
    val created_at: String,

    @SerialName("state_id")
    val stateId: Int,

    @SerialName("application_id")
    val applicationId: Int,

    @SerialName("state")
    val state: String,

    @SerialName("description")
    val description: String,
)