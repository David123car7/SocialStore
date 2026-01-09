package com.ipca.socialstore.presentation.views.item

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.ipca.socialstore.data.enums.ItemType
import com.ipca.socialstore.presentation.ui.components.IntroductionComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun CreateItemView(modifier: Modifier, navController: NavController){
    val viewModel : CreateItemViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    var scanActivated by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getItems()
    }
    if(scanActivated){
        SimpleBarcodeScanner(
            context = context,
            onBarcodeScanned = { barcode ->
                viewModel.getItemByCode(barCode = barcode)
                Toast.makeText(context, "Código lido: $barcode", Toast.LENGTH_SHORT).show()
                scanActivated = false
            },
            onCancel = {scanActivated = false}
        )
    }
    else{
        CreateItemViewContent(
            modifier = modifier,
            uiState = uiState,
            onItemNameUpdate = { value -> viewModel.updateItemName(value) },
            onItemTypeUpdate = { value -> viewModel.updateItemType(value) },
            onUpdateList = { index, date, qty ->
                viewModel.updateMapDate(index, date, qty)
            },
            onAddFields = { viewModel.addNewFields() },
            onClickCreate = {viewModel.createItem() },
            onRemoveFields = {value -> viewModel.removeFields(value)},
            onScanBarcode =  {
                scanActivated = true
            },
            onItemBarcodeUpdate = {code -> viewModel.updateBarCode(code = code)},
            onSearchItem = {value -> viewModel.filterItem(value)}
        )
    }
}

@Composable
fun CreateItemViewContent(
    modifier: Modifier = Modifier,
    uiState: ItemState,
    onItemNameUpdate: (String) -> Unit,
    onItemTypeUpdate: (String) -> Unit,
    onItemBarcodeUpdate: (String) -> Unit,
    onUpdateList: (Int, String?, String?) -> Unit,
    onAddFields: () -> Unit,
    onRemoveFields: (Int) -> Unit,
    onClickCreate: () -> Unit,
    onScanBarcode: () -> Unit,
    onSearchItem : (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    var isBarcodeVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Alimentação") }
    val isAlimentacao = uiState.item.itemType.lowercase() == "alimentação"

    LaunchedEffect(uiState.item.barCode) {
        if (!uiState.item.barCode.isNullOrBlank()) {
            isBarcodeVisible = true
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        IntroductionComponent(
            tittle = "Criar Item",
            description = "Preencha os dados abaixo ou use o scanner.",
            icon = Icons.Filled.Storage
        )

        OutlinedButton(
            onClick = onScanBarcode,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Ler Código de Barras (Câmara)")
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Inserir código manualmente?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Switch(
                    checked = isBarcodeVisible,
                    onCheckedChange = { isBarcodeVisible = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            androidx.compose.animation.AnimatedVisibility(visible = isBarcodeVisible) {
                TextFieldStringComponent(
                    value = uiState.item.barCode ?: "",
                    onValueUpdate = onItemBarcodeUpdate,
                    label = "Código de Barras",
                    icon = Icons.Default.QrCode,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextFieldStringComponent(
                value = uiState.item.name,
                onValueUpdate = {value -> onItemNameUpdate(value)
                                onSearchItem(value)},
                label = "Nome do Item",
                icon = Icons.Default.Label,
                modifier = Modifier,
            )

            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                OutlinedTextField(
                    value = uiState.item.itemType.ifBlank { "Selecionar Categoria" },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, "Abrir Menu")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expanded = !expanded }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    ItemType.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text( text = category.name) },
                            onClick = {
                                selectedOption = category.name
                                expanded = false
                                onItemTypeUpdate(category.name)
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Lotes e Validade",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                uiState.listDate.forEachIndexed { index, itemDate ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextFieldDateComponent(
                            modifier = Modifier.weight(1f),
                            label = if (isAlimentacao) "Data (Obrigatória)" else "Data (Opcional)",
                            date = itemDate.date ?: "",
                            onDateUpdate = { newValue ->
                                onUpdateList(index, newValue, null)
                            },
                            onDatePickerUpdate = {},
                        )

                        OutlinedTextField(
                            value = itemDate.quantity,
                            onValueChange = { newQty -> onUpdateList(index, null, newQty) },
                            label = { Text("Qtd") },
                            modifier = Modifier.weight(0.5f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        if (uiState.listDate.size > 1) {
                            IconButton(
                                onClick = { onRemoveFields(index) },
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "Remover",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                TextButton(
                    onClick = onAddFields,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text("Adicionar outro lote", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onClickCreate,
            colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(4.dp),
            enabled = run {
                val nameIsOk = uiState.item.name.isNotBlank()

                if (selectedOption.lowercase() == "alimentacao") {
                    val allDatesFilled = uiState.listDate.all { it.date.isNotBlank() }
                    nameIsOk && allDatesFilled
                } else {
                    nameIsOk
                }
            }
        ) {
            Icon(Icons.Filled.Save, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text(
                "Criar Item e Guardar Stock",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun SimpleBarcodeScanner(
    context: Context,
    onBarcodeScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    val scanner = remember { GmsBarcodeScanning.getClient(context) }

    LaunchedEffect(Unit) {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue = barcode.rawValue
                if (rawValue != null) {
                    onBarcodeScanned(rawValue)
                } else {
                    onCancel()
                }
            }
            .addOnCanceledListener {
                onCancel()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erro no scanner", Toast.LENGTH_SHORT).show()
                onCancel()
            }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("A abrir scanner...", modifier = Modifier.padding(top=60.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateItemPreview() {
    SocialStoreTheme {
        val mockState = ItemState(
            item = com.ipca.socialstore.data.models.ItemModel(
                name = "Azeite Virgem 1L",
                itemType = "Alimentação",
                barCode = "5601234567890"
            ),
            listDate = listOf(
                ExpirationDate(date = "12/10/2025", quantity = "10"),
                ExpirationDate(date = "05/02/2026", quantity = "24")
            )
        )

        CreateItemViewContent(
            modifier = Modifier.fillMaxSize(),
            uiState = mockState,
            onItemNameUpdate = {},
            onItemTypeUpdate = {},
            onItemBarcodeUpdate = {},
            onUpdateList = { _, _, _ -> },
            onAddFields = {},
            onRemoveFields = {},
            onClickCreate = {},
            onScanBarcode = {},
            onSearchItem = {}
        )
    }
}
