package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficiaryModel(

    val id : Int? = null,

    @SerialName("createdAt")
    val createdAt : String,

    @SerialName("uid")
    val uid : Int,
)