package com.ipca.socialstore.data.models

import java.util.Date

data class ItemModel (
    val itemName : String? = null,
    val itemType : String? = null, //create a object based type
    val expirationDate : Date? = null
)