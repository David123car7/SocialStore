package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class  DonationModel(
    val id : Int? = null,

    @SerialName("donation_date")
    val date : String,

    @SerialName("campaign_id")
    val campaignId: Int?,

    @SerialName("donor_name")
    val donorName : String
)
