package com.ipca.socialstore.presentation.models

import com.ipca.socialstore.data.models.ItemModel

data class StockReceiverModel(
    val item : ItemModel,
    val stockId : Int,
    var totalQuantity : Int,
    val quantityMap : MutableMap<String, Int> //maps the quantity with the expiration date
)