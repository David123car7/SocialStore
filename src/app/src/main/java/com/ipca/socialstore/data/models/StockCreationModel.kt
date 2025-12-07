package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockCreationModel(

    @SerialName("expiration_date")
    val expirationDate : String,

    @SerialName("quantity")
    val quantity : Int
)