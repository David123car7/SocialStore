package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationModel(
    val id: Int? = null,

    @SerialName("schoolyear")
    val schoolYear: Int,

    @SerialName("name")
    val name: String,

    @SerialName("birthDate")
    val birthDate: String,

    @SerialName("cc")
    val cc: String,

    @SerialName("phoneNumber")
    val phoneNumber: String,

    @SerialName("email")
    val email: String,

    @SerialName("requestType")
    val requestType: String,

    @SerialName("state_id")
    val stateId: Int,

    @SerialName("academic_id")
    val academicId: Int?,
)