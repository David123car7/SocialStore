package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeliveryItemsModel (

    val id : Int? = null,

    @SerialName("delivery_id")
    val deliveryId : Int,

    @SerialName("stock_id")
    val stockId : Int,

    @SerialName("quantity")
    val quantity : Int
)