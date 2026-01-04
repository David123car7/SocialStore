package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficiaryModel(
    val id : Int? = null,

    @SerialName("name")
    val name : String,
    @SerialName("birth_date")
    val birthDate : String,
    @SerialName("academic_id")
    val academicId : Int?,
)