package com.ipca.socialstore.presentation.views.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme


@Composable
fun CreateItemView(modifier: Modifier, navController: NavController){

    val viewModel : CreateItemViewModel = hiltViewModel()
    val uiState by viewModel.uiState

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
        onRemoveFields = {value -> viewModel.removeFields(value)}
    )
}

@Composable
fun CreateItemViewContent(
    modifier: Modifier = Modifier,
    uiState: ItemState,
    onItemNameUpdate: (String) -> Unit,
    onItemTypeUpdate: (String) -> Unit,
    onUpdateList: (Int, String?, String?) -> Unit,
    onAddFields: () -> Unit,
    onRemoveFields: (Int) -> Unit,
    onClickCreate: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = uiState.item.name,
            onValueChange = onItemNameUpdate,
            label = { Text("Nome do Item") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = uiState.item.itemType!!,
            onValueChange = onItemTypeUpdate,
            label = { Text("Tipo de Item") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        HorizontalDivider()

        Text(
            "Lotes de Validade",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start).padding(vertical = 12.dp)
        )

        uiState.listDate.forEachIndexed { index, itemDate ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = itemDate.date,
                    onValueChange = { newValue -> onUpdateList(index, newValue, null) },
                    label = { Text("Data") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    placeholder = { Text("DD/MM/AAAA") }
                )
                TextField(
                    value = itemDate.quantity,
                    onValueChange = { newQty -> onUpdateList(index, null, newQty) },
                    label = { Text("Qtd") },
                    modifier = Modifier.weight(0.6f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (uiState.listDate.size > 1) {
                    IconButton(onClick = { onRemoveFields(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        TextButton(
            onClick = onAddFields,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Adicionar outra data")
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onClickCreate,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.item.name.isNotBlank() && uiState.listDate.any { it.date.isNotBlank() }
        ) {
            Text("Criar Item e Guardar Stock")
        }
    }
}


/*
@Preview(showBackground = true, name = "Criar Item - Vazio")
@Composable
fun PreviewCreateItemEmpty() {
    // Simulando um estado inicial vazio
    val emptyState = ItemState(
        item = ItemModel(name = "", itemType = ""),
        isLoading = false,
        error = null
    )

    SocialStoreTheme {
        CreateItemViewContent(
            modifier = Modifier.fillMaxSize(),
            uiState = emptyState,
            onItemNameUpdate = {},
            onItemTypeUpdate = {},
            onClickCreate = {},
            onUpdateQuantity = {},
            onUpdateDate = {},
            onUpdateMap = {},

        )
    }
}

@Preview(showBackground = true, name = "Criar Item - Preenchido")
@Composable
fun PreviewCreateItemFilled() {
    // Simulando um estado com dados já inseridos
    val filledState = ItemState(
        item = ItemModel(name = "Arroz Agulha", itemType = "Alimentação"),
        isLoading = false,
        error = null
    )

    SocialStoreTheme {
        CreateItemViewContent(
            modifier = Modifier.fillMaxSize(),
            uiState = filledState,
            onItemNameUpdate = {},
            onItemTypeUpdate = {},
            onClickCreate = {},
            onUpdateQuantity = {},
            onUpdateDate = {},
            onUpdateMap = {}
        )
    }
}
*/
