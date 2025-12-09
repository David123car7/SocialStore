package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StockModel(

    val stock_id: Int? = null,

    @SerialName("item_id")
    val itemId : Int,

    @SerialName("expiration_date")
    val expirationDate : String,

    @SerialName("quantity")
    val quantity : Int,

    @SerialName("stock_id")
    val stockId : Int? = null
)

fun StockModel.isValid() : Boolean{
    return this.expirationDate.isNotEmpty() && this.quantity >= 0
}
