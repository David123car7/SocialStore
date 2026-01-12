package com.ipca.socialstore.presentation.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationReceiverModel(
    val id: Int? = null,
    @SerialName("tittle")
    val tittle: String,
    @SerialName("description")
    val description: String,
    @SerialName("read")
    val read: Boolean,
    @SerialName("type")
    val type: String,
    @SerialName("created_at")
    val created_at: String,
)