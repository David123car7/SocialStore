package com.ipca.socialstore.presentation.views.stock.detail

import androidx.compose.ui.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.presentation.models.StockReceiverModel
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.room.util.copy
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent2
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA


@Composable
fun StockItemDetailView(
    navController: NavController,
    modifier: Modifier,
) {
    val viewModel: StockItemDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    StockItemDetail(
        modifier = modifier,
        uiState = uiState,
        onUpdateQuantity = { value, date -> viewModel.updateBatchQuantity(dateKey = date, newQuantityStr = value)},
        onSave = {
            viewModel.updateStock()
        },
        onRemoveBatch = { date ->
            viewModel.removeStock(date = date)
        },
        onAddBatch = { date, quantity ->
            viewModel.addStock(date = date, qnt = quantity)
        },
        onUpdateName = viewModel::updateItemName,
        onUpdateType = viewModel::updateItemType,
        onUpdateBarcode = viewModel::updateItemBarcode
    )
}

@Composable
fun StockItemDetail(
    modifier: Modifier = Modifier, // Mudei para ter um valor default
    uiState: StockItemDetailState,
    onUpdateQuantity: (newQuantity: String, dateKey: String) -> Unit,
    onSave: () -> Unit,
    onRemoveBatch: (dateKey: String) -> Unit,
    onAddBatch: (date: String,quantity: Int) -> Unit,
    onUpdateName: (value: String) -> Unit,
    onUpdateType: (value: String) -> Unit,
    onUpdateBarcode: (value: String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    val flattenedStock = remember(uiState.stock.quantityMap) {
        uiState.stock.quantityMap.flatMap { it.entries }.sortedBy { it.key }
    }

    // 1. Usamos BOX em vez de Scaffold para empilhar o botão sobre o conteúdo
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // --- CONTEÚDO COM SCROLL ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Header Card
            ItemHeaderCard(
                item = uiState.stock.item,
                totalQuantity = uiState.stock.totalQuantity,
                isEditing = isEditing,
                onUpdateName = onUpdateName,
                onUpdateType = onUpdateType,
                onUpdateBarcode = onUpdateBarcode
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // 2. Stock Batches
            Text(
                text = "Lotes em Stock",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (flattenedStock.isEmpty()) {
                EmptyStateMessage()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    flattenedStock.forEach { entry ->
                        StockBatchRow(
                            date = entry.key,
                            quantity = entry.value,
                            isEditing = isEditing,
                            onQuantityChange = { newQty -> onUpdateQuantity(newQty, entry.key) },
                            onRemove = { onRemoveBatch(entry.key) }
                        )
                    }
                }
            }

            if (!isEditing) {
                AddBatchSection(onAdd = onAddBatch)
            }

            // IMPORTANTE: Espaço extra no fundo para o FAB não tapar o último item
            Spacer(modifier = Modifier.height(80.dp))
        }

        // --- FLOATING ACTION BUTTON (Manual) ---
        FloatingActionButton(
            onClick = {
                if (isEditing) onSave()
                isEditing = !isEditing
            },
            containerColor = if (isEditing) GreenIPCA else MaterialTheme.colorScheme.primaryContainer,
            // 2. Alinhamento manual no canto inferior direito
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // Margem padrão do Material Design
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                contentDescription = if (isEditing) "Guardar" else "Editar"
            )
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun ItemHeaderCard(
    item: ItemModel, // Replace with your actual ItemModel class
    totalQuantity: Int,
    isEditing: Boolean,
    onUpdateName: (String) -> Unit,
    onUpdateType: (String) -> Unit,
    onUpdateBarcode: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = GreenIPCA,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Detalhes do Produto",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    if (!isEditing) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                // Total Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = GreenIPCA.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Total: $totalQuantity",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = GreenIPCA,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (isEditing) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = item.name,
                    onValueChange = onUpdateName,
                    label = { Text("Nome do Produto") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                    OutlinedTextField(
                        value = item.itemType,
                        onValueChange = onUpdateType,
                        label = { Text("Categoria") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = item.barCode ?: "",
                        onValueChange = onUpdateBarcode,
                        label = { Text("Cód. Barras") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            Icon(Icons.Default.QrCode, contentDescription = null)
                        }
                    )
                }
            } else {
                // View Mode Details
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DetailItem(icon = Icons.Outlined.Category, label = "Categoria", value = item.itemType)
                    DetailItem(icon = Icons.Outlined.QrCode, label = "Código", value = item.barCode ?: "N/A")
                }
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon( imageVector = icon, contentDescription = null,modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StockBatchRow(
    date: String,
    quantity: Int,
    isEditing: Boolean,
    onQuantityChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val isExpiredOrNoDate = date == "9999-12-31"
    val displayDate = if (isExpiredOrNoDate) "Sem Validade" else date
    val dateColor = if (isExpiredOrNoDate) Color.Gray else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = if(isEditing) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Section
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            VerticalDivider(modifier = Modifier.padding(horizontal = 12.dp))

            // Quantity Section
            if (isEditing) {
                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = onQuantityChange,
                    label = { Text("Qtd") },
                    modifier = Modifier.width(100.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                }
            } else {
                Text(
                    text = "$quantity un.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenIPCA
                )
            }
        }
    }
}

@Composable
fun AddBatchSection(
    onAdd: (date:String, qnt: Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var newDate by remember { mutableStateOf("") }
    var newQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = (if (isExpanded) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else Color.Transparent),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        if (!isExpanded) {
            TextButton(
                onClick = { isExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = GreenIPCA)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar Novo Lote", color = GreenIPCA)
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Novo Lote", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    TextFieldDateComponent2(
                        modifier = Modifier.weight(1f).padding(5.dp),
                        label = "Data",
                        date = newDate,
                        onDateUpdate = {newDate = it},
                        onDatePickerUpdate = {}
                    )
                    OutlinedTextField(
                        value = newQuantity,
                        onValueChange = { newQuantity = it },
                        label = { Text("Qtd") },
                        modifier = Modifier.weight(0.5f).padding(5.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { isExpanded = false }) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if(newQuantity.isNotEmpty()) {
                                onAdd(newDate, newQuantity.toIntOrNull() ?: 0)
                                isExpanded = false
                                newDate = ""
                                newQuantity = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA)
                    ) {
                        Text("Adicionar")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Inventory,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sem stock registado",
            style = MaterialTheme.typography.bodyMedium,
        )
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

}

