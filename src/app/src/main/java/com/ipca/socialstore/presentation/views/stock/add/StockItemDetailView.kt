package com.ipca.socialstore.presentation.views.stock.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ipca.socialstore.data.models.StockHelper


@Composable
fun StockItemDetail(
    modifier: Modifier,
    uiState: StockHelper
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = "Nome : ${uiState.item.name}")
            Text(text = "Tipo Item : ${uiState.item.itemType}")
            Text(text = "Quantidade Total: ${uiState.quantity}")
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(text = "Datas de Validade:", fontWeight = FontWeight.Bold)
            }

            items(uiState.expirationDate.toList()){ (quantity, date) ->
                Text(text = " - Quantidade: $quantity | Validade: $date")
            }

        }
    }
}