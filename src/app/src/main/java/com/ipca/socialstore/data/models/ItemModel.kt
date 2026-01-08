package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemModel(
    val id: Int? = null,

    @SerialName("name")
    val name : String,
    @SerialName("bar_code")
    val barCode : String,

    @SerialName("item_type")
    val itemType : String,
)

fun ItemModel.isValid() : Boolean{
    return this.name.isNotEmpty()
}