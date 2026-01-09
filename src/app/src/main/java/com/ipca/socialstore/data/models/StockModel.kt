package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockModel(

    val id: Int? = null,

    @SerialName("item_id")
    val itemId : Int,

    @SerialName("expiration_date")
    val expirationDate : String,

    @SerialName("quantity")
    val quantity : Int,
)

