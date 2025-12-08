package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemModelCreation (
    @SerialName("name")
    val name : String,

    @SerialName("item_type")
    val itemType : String,
)