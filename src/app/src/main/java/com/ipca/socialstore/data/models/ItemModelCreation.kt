package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName

data class ItemModelCreation (
    @SerialName("name")
    val name : String,

    @SerialName("item_type")
    val itemType : String,
)