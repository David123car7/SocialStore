package com.ipca.socialstore.data.models

data class StockHelper(
    val item : ItemModel,
    val quantity : Int,
    val expirationDate : MutableMap<Int, String>//Int é a quantiade do item solo sem juntar
)