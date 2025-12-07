package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemModel(
    @SerialName("name")
    val name : String,

    @SerialName("item_type")
    val itemType : String,

    @SerialName("item_id")
    val itemId : Int

)

fun ItemModel.isValid() : Boolean{
    return this.name.isNotEmpty()
}