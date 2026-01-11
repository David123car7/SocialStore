package com.ipca.socialstore.presentation.models

import com.ipca.socialstore.data.models.ItemModel

data class StockReceiverModel(
    val item : ItemModel,
    val stockId : Int,
    var totalQuantity : Int,
    val quantityMap: Map<String, Int>
)

data class StockReceiverModel2(
    val item : ItemModel,
    var totalQuantity : Int,
    val quantityMap: List<Map<String, Int>>
)

fun initializeStockReceiverModel2(): StockReceiverModel2{
    return StockReceiverModel2(
        item = ItemModel(
            id = -1,
            name = "",
            barCode = "",
            itemType = ""
        ),
        totalQuantity = -1,
        quantityMap = emptyList(),
    )
}