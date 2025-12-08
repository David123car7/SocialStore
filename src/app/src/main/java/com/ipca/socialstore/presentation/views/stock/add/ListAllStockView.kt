package com.ipca.socialstore.presentation.views.stock.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.StockHelper

import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun GetAllStockView(modifier: Modifier, navController: NavController){

    val viewModel : ListAllStockViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    LaunchedEffect(Unit) {
        viewModel.getAllStock()
        println(uiState.stock)
    }
    GetAllStockViewContent(
        modifier,
        uiState,
        onItemClick = { value -> viewModel.selectStock(value)
        navController.navigate("stock_detail")}
    )
}

@Composable
fun GetAllStockViewContent(
    modifier: Modifier,
    uiState: GetStockState,
    onItemClick :(StockHelper) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = uiState.items ?: emptyList(),
            ) { index, stockHelper ->
                SingleItemStock(
                    modifier = Modifier,
                    uiState = stockHelper,
                )
            }
        }
    }
}

@Composable
fun SingleItemStock(
    modifier: Modifier,
    uiState: StockHelper
){
    Column(
        modifier = Modifier.padding(18.dp)
    ) {

        Text(text = "Nome : ${uiState.item.name}")
        Text(text = "Tipo Item : ${uiState.item.itemType}")
        Text(text = "Quantidade Total: ${uiState.quantity}")

        Spacer(modifier = Modifier.height(6.dp))

        Text(text = "Datas de Validade:", fontWeight = FontWeight.Bold)

        uiState.expirationDate.forEach { (qty, date) ->
            Text(text = " - Quantidade: $qty | Validade: $date")
        }
    }
}

@Preview(showBackground = true)
@Composable

fun PreviewGetAllStock(){
    SocialStoreTheme() {
        val uiState = GetStockState(null,null,false,null)
        GetAllStockViewContent(Modifier,uiState, onItemClick = { Unit})
    }
}

