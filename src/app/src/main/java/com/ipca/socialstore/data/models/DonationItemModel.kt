package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DonationItemModel(
    @SerialName("item_id")
    val itemId : String,

    @SerialName("donation_id")
    val donationId : String,
)