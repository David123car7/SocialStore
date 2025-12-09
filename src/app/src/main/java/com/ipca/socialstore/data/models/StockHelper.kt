package com.ipca.socialstore.data.models

data class StockHelper(
    val item : ItemModel,
    val stockId : Int,
    var quantity : Int,
    val expirationDate : MutableMap<Int, String>
)