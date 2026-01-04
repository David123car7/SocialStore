package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficiaryModel(

    val id : Int? = null,

    @SerialName("created_At")
    val createdAt : String,
    @SerialName("name")
    val name : String,
    @SerialName("birth_date")
    val birthDate : String,
    @SerialName("address_id")
    val addressId : Int
)