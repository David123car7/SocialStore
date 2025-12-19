package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationScheduledModel (

    val id : Int? = null,

    val createdAt : String? = null,

    @SerialName("subject")
    val subject: String,

    @SerialName("is_Read")
    val isRead : Boolean,

    @SerialName("title")
    val title : String?
)