package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class  DonationModel(
    @SerialName("donation_date")
    val donationDate : String?,

    @SerialName("campaign_id")
    val campaignId: String?,

    @SerialName("donation_id")
    val donationId : String?,
)
