package com.ipca.socialstore.presentation.views.stock.List
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.ItemModel

import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme


@Composable
fun StockItemDetailView(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel : ListAllStockViewModel
) {

    val stock = viewModel.uiState

    if (stock == null) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Detalhe do item de stock não encontrado.")
        }
        return
    }

    StockItemDetail(
        modifier = modifier,
        uiState = stock.value.selectedStock!!,
        onClickEdit = {},

    )
}
@Composable
fun StockItemDetail(
    modifier: Modifier,
    uiState: StockReceiverModel,
    onClickEdit : () -> Unit,

    ){
    Scaffold(
        topBar = {}
    ) {

    }
}

@Preview(showBackground = true, name = "Stock Item Detail Preview")
@Composable
fun PreviewStockItemDetail() {
    // 1. Mock ItemData
    val mockItemData = ItemModel(
        name = "Caixa de Maçãs Gala",
        itemType = "Fruta"
    )

    // 2. Mock QuantityMap (Datas de Validade e Quantidades)
    val mockQuantityMap = mutableMapOf(
        "2025-12-25" to 50,
        "2026-01-10" to 120,
        "2026-01-30" to 30
    )

    // 3. Mock StockReceiverModel
    val mockSelectedItem = StockReceiverModel(
        item = mockItemData,
        stockId = 101,
        totalQuantity = 200, // 50 + 120 + 30
        quantityMap = mockQuantityMap
    )

    // A função de edição será apenas simulada
    val mockOnClickEdit: () -> Unit = { println("Botão Editar Clicado!") }

    SocialStoreTheme {
        StockItemDetail(
            modifier = Modifier.fillMaxSize(),
            uiState = mockSelectedItem,
            onClickEdit = mockOnClickEdit
        )
    }
}

