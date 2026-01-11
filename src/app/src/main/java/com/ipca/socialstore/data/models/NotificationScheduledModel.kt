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
    val isRead : Boolean = false,

    @SerialName("title")
    val title : String?,

    @SerialName("notification_key")
    val notificationKey : String,

    @SerialName("beneficiary_id")
    val beneficiaryId : Int? = null
)