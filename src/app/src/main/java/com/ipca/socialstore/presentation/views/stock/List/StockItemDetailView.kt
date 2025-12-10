package com.ipca.socialstore.presentation.views.stock.List
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockHelper
import com.ipca.socialstore.presentation.views.stock.Edit.EditStockViewModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme
import io.ktor.http.Url

@Composable
fun StockItemDetailView(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel : ListAllStockViewModel
) {

    val stock = viewModel.detail.value

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
        uiState = stock,
        onClickEdit = {},

    )
}
@Composable
fun StockItemDetail(
    modifier: Modifier,
    uiState: StockHelper,
    onClickEdit : () -> Unit,

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
                .padding(8.dp)
        ) {
            item {
                Text(text = "Datas de Validade:", fontWeight = FontWeight.Bold)
            }
            items(uiState.expirationDate.toList()){ (quantity, date) ->
                Text(text = " - Quantidade: $quantity | Validade: $date")
            }

            item {
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {onClickEdit()}
                ) {
                    Text("Editar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockItemDetailPreview() {
    SocialStoreTheme {

    }
}

