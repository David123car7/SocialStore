package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class NotificationUserModel(
    val id: Int? = null,
    @SerialName("tittle")
    val tittle: String,
    @SerialName("description")
    val description: String,
    @SerialName("read")
    val read: Boolean,
    @SerialName("created_at")
    val created_at: String,
    @SerialName("type")
    val type: String,
    @SerialName("user_id")
    val userId: String,
)