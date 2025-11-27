package com.ipca.socialstore.data.models

import java.util.Date

data class ItemModel (
    val productName : String? = null,
    val quantity : String? = null,
    val productType : String? = null,
    val entryData : Date? = null,
    val expirationDate : Date? = null
)