package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    @SerialName("id")
    val id: String?,

    @SerialName("profile_id")
    val profileId: Int,

    @SerialName("role")
    val role: String,

    @SerialName("application_id")
    val applicationId: Int?,
)
