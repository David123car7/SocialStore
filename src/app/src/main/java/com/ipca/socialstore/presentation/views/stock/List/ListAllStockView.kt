package com.ipca.socialstore.presentation.views.stock.List

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.presentation.models.StockReveiverModel
import com.ipca.socialstore.presentation.routes.AdminRoutes

import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun GetAllStockView(modifier: Modifier, navController: NavController, viewModel: ListAllStockViewModel){

    val uiState by viewModel.uiState
    LaunchedEffect(Unit) {
        viewModel.getAllStock()
    }
    GetAllStockViewContent(
        modifier,
        uiState,
        navController,
        onItemClick = { value -> viewModel.selectStock(value) }

    )
}

@Composable
fun GetAllStockViewContent(
    modifier: Modifier,
    uiState: GetStockState,
    navController : NavController,
    onItemClick :(StockReveiverModel) -> Unit
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
                    onClick = {onItemClick(stockHelper)
                               navController.navigate(AdminRoutes.SelectStock)
                              },
                    uiState = stockHelper,
                )
            }
        }
    }
}

@Composable
fun SingleItemStock(
    modifier: Modifier = Modifier,
    uiState: StockReveiverModel,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Nome: ${uiState.item.name}")
            Text("Tipo: ${uiState.item.itemType}")
            Text("Qtd: ${uiState.totalQuantity}")

        }
    }
}


@Preview(showBackground = true)
@Composable

fun PreviewGetAllStock(){
    SocialStoreTheme() {
        val uiState = GetStockState(null,null,false,null)
        GetAllStockViewContent(Modifier,uiState, onItemClick = { Unit}, navController = rememberNavController())
    }
}

