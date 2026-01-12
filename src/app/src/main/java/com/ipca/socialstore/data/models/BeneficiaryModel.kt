package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficiaryModel(
    val id : Int? = null,
    @SerialName("name")
    val name : String,
    @SerialName("phone_number")
    val phoneNumber : String,
    @SerialName("birth_date")
    val birthDate : String,
    @SerialName("academic_id")
    val academicId : Int?,
    @SerialName("missed_appointments")
    val missedAppointments: Int? = 0,
    @SerialName("state")
    val state : String? = "on_Day"
)

