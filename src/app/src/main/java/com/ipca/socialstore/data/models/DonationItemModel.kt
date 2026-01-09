package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DonationItemModel(
    val id: Int? = null,

    @SerialName("item_id")
    val itemId : Int,

    @SerialName("donation_id")
    val donationId : Int,

    @SerialName("quantity")
    val quantity : Int
)