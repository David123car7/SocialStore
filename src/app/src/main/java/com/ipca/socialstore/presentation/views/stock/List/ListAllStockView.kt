package com.ipca.socialstore.presentation.views.stock.List

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.* // Importações de Material3
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.presentation.ui.components.SearchBarContent
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import kotlin.collections.mutableMapOf

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
        onItemClick = { value -> viewModel.selectStock(value) },
        onSearchItem = {value -> viewModel.updateSearchList(value)},
        onSearchType = {value -> viewModel.updateSearchListType(value)},
        onGetItemId = {value -> viewModel.updateItemId(value)}
    )
}

@Composable
fun GetAllStockViewContent(
    modifier: Modifier,
    uiState: GetStockState,
    navController : NavController,
    onItemClick :(StockReceiverModel) -> Unit,
    onSearchItem: (value: String) -> Unit,
    onSearchType : (value : String) -> Unit,
    onGetItemId : (value : String) -> Unit,
) {
    val listToDisplay = uiState.searchResult ?: uiState.items

    Box(modifier = modifier.fillMaxSize()) {

        // 1. CONTEÚDO DINÂMICO (Loading, Erro ou Lista)
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }
            uiState.error != null -> {
                ErrorMessage(error = uiState.error)
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    StockList(
                        modifier = Modifier.weight(1f),
                        items = listToDisplay,
                        navController = navController,
                        uiState = uiState,
                        onItemClick = onItemClick,
                        onSearchItem = onSearchItem,
                        isSearchExecuted = uiState.searchResult != null,
                        onSearchType = onSearchType,
                        onGetItemId = onGetItemId,
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(AdminRoutes.CreateItem)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
        }
    }
}
@Composable
private fun StockList(
    modifier: Modifier,
    items: List<StockReceiverModel>?,
    navController: NavController,
    uiState: GetStockState,
    onItemClick: (StockReceiverModel) -> Unit,
    onSearchItem: (value : String) -> Unit,
    onSearchType : (value : String) -> Unit,
    isSearchExecuted: Boolean,
    onGetItemId : (value : String) -> Unit,
) {
    val selectedType = remember { mutableStateOf<String?>(null) }

    val itemTypes = remember(items) {
        items?.map { it.item.itemType }?.filter { it.isNotBlank() }?.distinct() ?: emptyList()
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            SearchBarContent { newValue -> onSearchItem(newValue) }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AssistChip(
                        onClick = {
                            selectedType.value = null
                            onSearchType("")
                        },
                        label = { Text("Todos") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedType.value == null)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                items(itemTypes) { type ->
                    val isSelected = selectedType.value == type
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newType = if (isSelected) null else type
                            selectedType.value = newType
                            onSearchType(newType ?: "")
                        },
                        label = { Text(type!!) },
                    )
                }
            }

            Text(
                text = "${items?.size ?: 0} produtos",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (items.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSearchExecuted) EmptySearchResultMessage() else EmptyListMessage()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(items) { _, stockHelper ->
                        SingleItemStock(
                            onClick = {
                                onItemClick(stockHelper)
                                onGetItemId(stockHelper.stockId.toString())
                                navController.navigate(AdminRoutes.SelectStock)
                            },
                            uiState = stockHelper,
                        )
                    }
                }
            }
        }

    }
}
@Composable
fun SingleItemStock(
    modifier: Modifier = Modifier,
    uiState: StockReceiverModel,
    onClick: () -> Unit
) {
    val quantity = uiState.totalQuantity
    val isLowStock = quantity <= 5
    val isOutOfStock = quantity == 0

    val containerColor = when {
        isOutOfStock -> MaterialTheme.colorScheme.errorContainer
        isLowStock -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        isOutOfStock -> MaterialTheme.colorScheme.onErrorContainer
        isLowStock -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = uiState.item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tipo: ${uiState.item.itemType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "${uiState.totalQuantity} UN",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(error: ErrorText?) {
    val errorString = error?.asString() ?: ""

    val displayMessage = if (errorString.contains("Not Found", ignoreCase = true)) {
        "Não existe stock disponível no momento."
    } else {
        errorString.ifBlank { "Erro ao carregar stock. Tente novamente." }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = displayMessage,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
fun EmptyListMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Nenhum item de stock disponível.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun EmptySearchResultMessage() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Não existe item.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
@Preview(showBackground = true, name = "Estado de Sucesso")
@Composable
fun PreviewGetAllStockSuccess() {
    val mockItem1 = ItemModel(name = "Arroz Agulha 1kg", itemType = "Alimentação")
    val mockItem2 = ItemModel(name = "Detergente Loiça", itemType = "Limpeza")
    val mockItem3 = ItemModel(name = "T-Shirt Branca L", itemType = "Vestuário")

    val mockStockList = listOf(
        StockReceiverModel(item = mockItem1, stockId = 1, totalQuantity = 50, quantityMap = mutableMapOf()),
        StockReceiverModel(item = mockItem2, stockId = 2, totalQuantity = 4, quantityMap = mutableMapOf()), // Stock Baixo
        StockReceiverModel(item = mockItem3, stockId = 3, totalQuantity = 0, quantityMap = mutableMapOf())  // Esgotado
    )

    // 2. Simular o estado de UI
    val uiState = GetStockState(
        items = mockStockList,
        isLoading = false,
        error = null,
        searchResult = null
    )

    SocialStoreTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GetAllStockViewContent(
                modifier = Modifier.fillMaxSize(),
                uiState = uiState,
                navController = rememberNavController(),
                onItemClick = {},
                onSearchItem = {},
                onSearchType = {},
                onGetItemId = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Estado de Carregamento")
@Composable
fun PreviewGetAllStockLoading() {
    val uiState = GetStockState(isLoading = true)

    SocialStoreTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GetAllStockViewContent(
                modifier = Modifier.fillMaxSize(),
                uiState = uiState,
                navController = rememberNavController(),
                onItemClick = {},
                onSearchItem = {},
                onSearchType = {},
                onGetItemId = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Estado Vazio")
@Composable
fun PreviewGetAllStockEmpty() {
    val uiState = GetStockState(items = emptyList(), isLoading = false)

    SocialStoreTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GetAllStockViewContent(
                modifier = Modifier.fillMaxSize(),
                uiState = uiState,
                navController = rememberNavController(),
                onItemClick = {},
                onSearchItem = {},
                onSearchType = {},
                onGetItemId = {}
            )
        }
    }
}


