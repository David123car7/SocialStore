package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DonationItem(
    @SerialName("donation_id")
    val donationId : String,

    @SerialName("item_id")
    val itemId : String,
)

fun DonationItem.isValid() : Boolean{
    return this.donationId.isNotEmpty() && this.itemId.isNotEmpty()
}