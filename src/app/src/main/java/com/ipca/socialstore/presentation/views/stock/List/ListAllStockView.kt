package com.ipca.socialstore.presentation.views.stock.List

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Importações de Material3
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.presentation.ui.components.SearchBarContent
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.utils.ErrorText
import kotlin.collections.mutableMapOf

import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import dagger.assisted.Assisted

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
    )
}

@Composable
fun GetAllStockViewContent(
    modifier: Modifier,
    uiState: GetStockState,
    navController : NavController,
    onItemClick :(StockReceiverModel) -> Unit,
    onSearchItem: (value: String) -> Unit,
    onSearchType : (value : String) -> Unit
) {
    val listToDisplay = uiState.searchResult ?: uiState.items

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorMessage(error = uiState.error)

            else -> StockList(
                items = listToDisplay,
                navController = navController,
                uiState = uiState,
                onItemClick = onItemClick,
                onSearchItem = onSearchItem,
                isSearchExecuted = uiState.searchResult != null,
                onSearchType = onSearchType,
            )
        }
    }
}


@Composable
private fun StockList(
    items: List<StockReceiverModel>?,
    navController: NavController,
    uiState: GetStockState,
    onItemClick: (StockReceiverModel) -> Unit,
    onSearchItem: (value : String) -> Unit,
    onSearchType : (value : String) -> Unit,
    isSearchExecuted: Boolean
) {
    val selectedType = remember { mutableStateOf<String?>(null) }

    val itemTypes = remember (items){
        items
            ?.map { it.item.itemType }
            ?.filter { it.isNotBlank() }
            ?.distinct()
            ?: emptyList()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            SearchBarContent {newValue -> onSearchItem(newValue)}

            Divider(modifier = Modifier.padding(bottom = 8.dp))
        }
        item{
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, top = 4.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip para limpar o filtro
                    item {
                        AssistChip(
                            onClick = {
                                selectedType.value = null
                                onSearchType("") // Limpa o filtro de tipo
                            },
                            label = { Text("Todos") },
                            leadingIcon = {
                                if (selectedType.value == null) {
                                    Icon(Icons.Filled.Search, contentDescription = null)
                                }
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedType.value == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }

                    // Chips para cada tipo de item
                    items(itemTypes) { type ->
                        val isSelected = selectedType.value == type
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val newType = if (isSelected) null else type
                                selectedType.value = newType
                                onSearchType(newType ?: "")
                            },
                            label = { Text(type) },
                        )
                    }
                }
            }
        }

        // CORREÇÃO: Contagem de itens
        item {
            Text("${items?.size ?: 0} produtos", style = MaterialTheme.typography.titleSmall)
        }

        // --- 3. CONTEÚDO DA LISTA ---
        if (items.isNullOrEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(top = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSearchExecuted) {
                        EmptySearchResultMessage()
                    } else {
                        EmptyListMessage()
                    }
                }
            }
        } else {
            itemsIndexed(items) { _, stockHelper ->
                SingleItemStock(
                    onClick = {
                        onItemClick(stockHelper)
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
                // Nome do Item (Destaque Principal)
                Text(
                    text = uiState.item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Spacer(Modifier.height(4.dp))
                // Tipo de Item (Detalhe Secundário)
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

// CORREÇÃO DE TIPAGEM: Receber ErrorText e usar asString()
@Composable
fun ErrorMessage(error: ErrorText?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            // Usa a função asString() da sealed class ErrorText
            text = error?.asString() ?: "Erro ao carregar stock. Tente novamente.",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(24.dp)
        )
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
        // Remova o fillMaxSize() aqui para que ele possa ser controlado pelo Box pai
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Não existe item.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewGetAllStock(){

    // É necessário ter o ItemModel (que é uma classe aninhada) disponível no seu projeto
    // Assumindo que você tem uma definição de ItemModel semelhante a:
    // data class ItemModel(val name: String, val itemType: String)

    // Criação de Mocks de ItemModel usando a referência completa
    val mockItemData1 = ItemModel(name = "Computador Portátil Pro", itemType = "Eletrónica")
    val mockItemData2 = ItemModel(name = "Teclado Mecânico RGB", itemType = "Periféricos")
    val mockItemData3 = ItemModel(name = "Webcam HD", itemType = "Periféricos")


    // Criação de Mocks de StockReveiverModel
    val mockStock1 = StockReceiverModel(
        item = mockItemData1,
        stockId = 101, // Usando stockId como definido no seu modelo
        totalQuantity = 15, // Stock Alto
        quantityMap = mutableMapOf("A1" to 10, "B2" to 5)
    )
    val mockStock2 = StockReceiverModel(
        item = mockItemData2,
        stockId = 102,
        totalQuantity = 3, // Stock Baixo (para ver a cor de alerta)
        quantityMap = mutableMapOf("C3" to 3)
    )
    val mockStock3 = StockReceiverModel(
        item = mockItemData3,
        stockId = 103,
        totalQuantity = 0, // Stock Esgotado
        quantityMap = mutableMapOf()
    )

    // Simulação do GetStockState (estado de sucesso, com dados)
    val uiStateSuccess = GetStockState(
        items = listOf(mockStock1, mockStock2, mockStock3),
        error = null,
        isLoading = false,
        selectedStock = null // Usando o campo 'selectedStock' introduzido anteriormente
    )

    // Simulação do GetStockState (estado de erro)
    val uiStateError = GetStockState(
        items = emptyList(),
        // Assumindo que ErrorText tem um .toString() útil ou que é um tipo simples
        error = ErrorText.DynamicString( "Erro 404: Não foi possível carregar os dados."),
        isLoading = false,
        selectedStock = null
    )

    SocialStoreTheme() {
        Column {
            // Preview 1: Lista de Stock (Sucesso)
            Text(
                text = "Lista de Stock (Sucesso)",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            GetAllStockViewContent(
                modifier = Modifier.height(300.dp),
                uiState = uiStateSuccess,
                onItemClick = { /* No-op para Preview */ },
                navController = rememberNavController(),
                onSearchItem = {},
                onSearchType = {}
            )
        }
    }
}


