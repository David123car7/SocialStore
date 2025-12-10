package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CampaignModel(
    val id: Int? = null,

    @SerialName("name")
    val name : String,

    @SerialName("date")
    val date : String
)

fun CampaignModel.isValid() : Boolean{
    return this.name.isNotEmpty()
}