package com.ipca.socialstore.presentation.views.basket.preparation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.ui.components.SearchBarContent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun BasketPreparationView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : BasketPreparationViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    BasketPreparationContent(
        modifier = modifier,
        uiState = uiState,
        onClickDate = {},
        onUpdateQuantity = {id, operation -> viewModel.updateItemQuantity(id,operation)},
        onCategorySelected = {value -> viewModel.onCategorySelected(value)},
        onSearchItem = {value -> viewModel.updateSearchList(value)},
        onCreate = {viewModel.createDeliveryService()},
        onSelectDate = {value -> viewModel.updateSchedulingId(value)}
    )

    LaunchedEffect(uiState.selectScheduling) {
        viewModel.fetchInfo()
    }
}

@Composable
fun BasketPreparationContent(
    modifier: Modifier,
    uiState: BasketPreparationState,
    onClickDate: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onUpdateQuantity: (stockId: Int, operation: Int) -> Unit,
    onSearchItem :(String) -> Unit,
    onCreate: () -> Unit,
    onSelectDate: (SchedulingModel) -> Unit
) {
    val scrollState = rememberScrollState()
    val beneficiary = uiState.beneficiary ?: return
    var expanded by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Selecionar Data") }
    var showWarning by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = beneficiary.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Processo nº ${beneficiary.id}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

        Column {
            Text("Selecione a Data do Agendamento", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Box(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = selectedDate)
                    Icon(Icons.Default.ArrowDropDown, null)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    uiState.scheduling?.filter { it.state == "accept" }?.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.schedulingDate) },
                            onClick = {
                                selectedDate = item.schedulingDate
                                onClickDate(item.schedulingDate)
                                onSelectDate(item)
                                expanded = false
                                showWarning = false
                            }
                        )
                    }
                }
            }
        }

        if(showWarning){
            Text(
                text = "Por favor, selecione uma data antes de continuar",
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
        Text("Adicionar Itens ao Cabaz", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        SearchBarContent { value -> onSearchItem(value)}

        Column {
            Text("Filtrar por Categoria", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val categories = listOf("Tudo", "Alimentação", "Higiene", "Limpeza")
                items(categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF136342),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
        uiState.filteredList?.forEach { item ->
            BasketItemRow(
                itemName = item.item.name,
                currentQty = uiState.selectedQuantities[item.stockId] ?: 0,
                onIncrease = { onUpdateQuantity(item.stockId, 1) },
                onDecrease = { onUpdateQuantity(item.stockId, -1) }
            )
        }

        Spacer(modifier = Modifier.height(80.dp))


        Button(
            onClick = {
                if (uiState.selectScheduling == null){
                    showWarning = true
                }else{
                    onCreate()
                }
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Colocar Items Entrega")
        }
    }
}

@Composable
fun BasketItemRow(
    itemName: String,
    currentQty: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = itemName, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, enabled = currentQty > 0) {
                    Text("-", style = MaterialTheme.typography.headlineSmall)
                }
                Text(
                    text = currentQty.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onIncrease) {
                    Text("+", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF136342))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BasketPreparationPreview() {
    SocialStoreTheme {

        val mockUiState = BasketPreparationState(
            beneficiary = BeneficiaryModel(
                id = 5,
                name = "David Amorim Carvalho",
                phoneNumber = "",
                birthDate = "",
                academicId = 1
            ),
            scheduling = listOf(
                SchedulingModel(
                    schedulingDate = "2026-01-20",
                    state = "accept",
                    beneficiaryId = 5,
                    reason = null,
                    note = ""
                ),
               SchedulingModel(
                    schedulingDate = "2026-01-25",
                   state = "accept",
                   beneficiaryId = 5,
                   reason = null,
                   note = ""
                )
            ),
            filteredList = listOf(
                StockReceiverModel(
                    item = ItemModel(name = "Arroz 1kg", itemType = "Alimentação"),
                    stockId = 1,
                    totalQuantity = 50,
                    quantityMap = emptyMap()
                ),
                StockReceiverModel(
                    item = ItemModel(name = "Leite UHT 1L", itemType = "Alimentação"),
                    stockId = 2,
                    totalQuantity = 24,
                    quantityMap = emptyMap()
                ),
                StockReceiverModel(
                    item = ItemModel(name = "Sabonete", itemType = "Higiene"),
                    stockId = 3,
                    totalQuantity = 10,
                    quantityMap = emptyMap()
                )
            )
        )

        BasketPreparationContent(
            modifier = Modifier.padding(top = 16.dp),
            uiState = mockUiState,
            onClickDate = {},
            onCategorySelected = {},
            onUpdateQuantity = { _, _ -> },
            onSearchItem = {},
            onCreate = {},
            onSelectDate = {}
        )
    }
}