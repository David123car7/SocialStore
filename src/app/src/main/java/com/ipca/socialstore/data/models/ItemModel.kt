package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemModel(
    @SerialName("name")
    val name : String,

    @SerialName("item_type")
    val itemType : String?
)

fun ItemModel.isValid() : Boolean{
    return this.name.isNotEmpty()
}