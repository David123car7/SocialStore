package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationSchedulingModel(

    val id : Int? = null,

    @SerialName("created_at")
    val createdAt : String,

    @SerialName("subject")
    val subject : String,

)