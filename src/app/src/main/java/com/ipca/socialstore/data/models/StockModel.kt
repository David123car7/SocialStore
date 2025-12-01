package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockModel(

    @SerialName("item_id")
    val itemId : String,

    @SerialName("expiration_date")
    val expirationDate : String,

    @SerialName("quantity")
    val quantity : Int
)

fun StockModel.isValid() : Boolean{
    return this.itemId.isNotEmpty() && this.expirationDate.isNotEmpty() && this.quantity >= 0
}
