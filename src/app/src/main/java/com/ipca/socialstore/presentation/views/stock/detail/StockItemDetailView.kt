package com.ipca.socialstore.presentation.views.stock.detail
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.ItemModel

import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.views.stock.List.GetStockState
import com.ipca.socialstore.presentation.views.stock.List.ListAllStockViewModel


@Composable
fun StockItemDetailView(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel : ListAllStockViewModel
) {
    val stock = viewModel.uiState
    StockItemDetail(
        modifier = modifier,
        uiState = stock.value.selectedStock!!,
        uiStateEdit = stock.value,
        onUpdateQuantity = { value,date -> viewModel.updateQuantity(value,date)},
        onUpdateDate = { value -> viewModel.updateDate(value)},
        onSave = {viewModel.saveStockChanges()},
        onRemove = {value -> viewModel.removeStock(value)},
        onAdd = {value -> viewModel.addStock(value)},
        onEdit = {
            val routeName = AdminRoutes.EditItem::class.qualifiedName!!
            navController.navigate("$routeName/${stock.value.itemId}")
        }
    )
}

@Composable
fun StockItemDetail(
    modifier: Modifier = Modifier,
    uiState: StockReceiverModel,
    uiStateEdit: GetStockState,
    onUpdateQuantity: (value: String, date: String) -> Unit,
    onUpdateDate: (value: String) -> Unit,
    onSave: () -> Unit,
    onEdit:() -> Unit,
    onRemove: (value : String) -> Unit,
    onAdd : (value : Int) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var showAddFields by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.item.itemType!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(onClick = onEdit) {
                        Text("Editar")
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(text = "Total: ${uiState.totalQuantity}", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Lotes por Validade", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            items(uiState.quantityMap.toList()) { (date, quantity) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (date == "9999-12-31") "Sem validade" else date,
                            modifier = Modifier.weight(1f),
                            style = if (date == "9999-12-31")
                                MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            else MaterialTheme.typography.bodyMedium
                        )
                        Text(text = "$quantity unid.", fontWeight = FontWeight.SemiBold)

                        IconButton(onClick = { onRemove(uiState.stockId.toString()) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remover lote",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = true) {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                if (showAddFields) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = uiStateEdit.date ?: "",
                            onValueChange = { onUpdateDate(it) },
                            label = { Text("Nova Data") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("AAAA-MM-DD") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = uiStateEdit.quantity ?: "",
                            onValueChange = { onUpdateQuantity(it, uiStateEdit.date ?: "") },
                            label = { Text("Qtd") },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        IconButton(onClick = { showAddFields = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                        }
                        IconButton(onClick = {
                            onAdd(uiState.item.id!!)
                            showAddFields = false }) {
                            Icon(Icons.Default.Check, contentDescription = "Adicionar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            onUpdateDate("")
                            showAddFields = true
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Adicionar Lote")
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true, name = "Stock Item Detail Preview")
@Composable
fun PreviewStockItemDetail() {
    // 1. Mock Dados do Item (Arroz conforme a tua imagem)
    val mockItemData = ItemModel(
        name = "arroz",
        itemType = "alimentação",
        barCode = ""
    )

    // 2. Mock do Mapa de Quantidades (Lotes atuais)
    val mockQuantityMap = mapOf(
        "2025/10/17" to 200,
        "2025/10/15" to 66
    )

    val mockSelectedItem = StockReceiverModel(
        item = mockItemData,
        stockId = 101,
        totalQuantity = 266,
        quantityMap = mockQuantityMap
    )

    val mockUiStateEdit = GetStockState(
        date = "2025/10/17",
        quantity = "20",
        isEditing = true
    )

    SocialStoreTheme {
        Scaffold { padding ->
            StockItemDetail(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                uiState = mockSelectedItem,
                uiStateEdit = mockUiStateEdit,
                onUpdateQuantity = { value, date ->
                    println("Preview Update: $date -> $value")
                },
                onUpdateDate = { println("Preview Date Update: $it") },
                onSave = { println("Preview Save Clicked") },
                onRemove = {},
                onAdd = {},
                onEdit = {}
            )
        }
    }
}

