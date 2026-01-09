package com.ipca.socialstore.presentation.views.donation.create

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.presentation.models.CreateDonationHelperModel
import com.ipca.socialstore.presentation.models.DonationHelperModel
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun CreateDonationView(modifier: Modifier, navController: NavController) {

    val viewModel: CreateDonationViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getCampaigns()
    }
    LaunchedEffect(Unit) {
        viewModel.getItems()
    }
    CreateDonationViewContent(
        modifier,
        uiState,
        onUpdateDate = { newValue -> viewModel.updateDonationDate(newValue) },
        onClickCreate = { viewModel.createDonation() },
        onUpdateName = { newValue -> viewModel.updateItemName(newValue) },
        onUpdateItemType = { newValue -> viewModel.updateItemType(newValue) },
        onUpdateExpiration = { index, newValue -> viewModel.updateExpiration(index, newValue) },
        onUpdateQuantity = { index, newValue -> viewModel.updateQuantity(index = index, value = newValue) },
        onAddNewDate = { viewModel.onAddNewDate() },
        onAddItemHelper = { viewModel.addItemToDonationHelper() },
        onUpdateDonor = {value -> viewModel.updateDonorName(value)},
        onSearchItem = {value -> viewModel.filterItem(value)}
    )
}

@Composable
fun CreateDonationViewContent(
    modifier: Modifier,
    uiState: DonationState,
    onUpdateDate: (newValue: String) -> Unit,
    onUpdateName: (newValue: String) -> Unit,
    onUpdateItemType: (newValue: String) -> Unit,
    onUpdateQuantity: (Int, newValue: String) -> Unit,
    onUpdateExpiration: (Int, newValue: String) -> Unit,
    onClickCreate: () -> Unit,
    onAddNewDate: () -> Unit,
    onAddItemHelper: () -> Unit,
    onUpdateDonor : (String) -> Unit,
    onSearchItem :(String) -> Unit
) {
    var selectedCampaignName by remember { mutableStateOf("Selecionar Campanha") }
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Nova Entrada de Doação",
            style = MaterialTheme.typography.headlineSmall,
            color = GreenIPCA,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCampaignName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Filtrar por Campanha") },
                        modifier = Modifier.fillMaxWidth(),
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
                        DropdownMenuItem(
                            text = { Text("Todas as Campanhas") },
                            onClick = {
                                selectedCampaignName = "Todas as Campanhas"
                                expanded = false
                            }
                        )

                        HorizontalDivider()
                        uiState.campaigns.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.name) },
                                onClick = {
                                    selectedCampaignName = item.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.donorName,
                        label = { Text("Doado Por") },
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onUpdateDonor(value) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    var showDatePicker by remember { mutableStateOf(false) }

                    TextFieldDateComponent(
                        modifier = Modifier.weight(1f),
                        label = "Doado Dia",
                        date = uiState.donationDate,
                        onDateUpdate = { newValue -> onUpdateDate(newValue) },
                        onDatePickerUpdate = { showDatePicker = true }
                    )

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    val millis = datePickerState.selectedDateMillis
                                    if (millis != null) {
                                        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                        onUpdateDate(formatter.format(java.util.Date(millis)))
                                    }
                                    showDatePicker = false
                                }) { Text("OK") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.itemName,
                        label = { Text("Nome Item") },
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onUpdateName(value)
                                        onSearchItem(value)
                                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.itemType,
                        label = { Text("Tipo Item") },
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onUpdateItemType(value) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                Text(
                    text = "Informações Item",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                uiState.listDate.forEachIndexed { index, date ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = date.date,
                            label = { Text("Validade") },
                            modifier = Modifier.weight(1f),
                            onValueChange = { value -> onUpdateExpiration(index, value) },
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = date.quantity,
                            label = { Text("Quantidade") },
                            modifier = Modifier.weight(1f),
                            onValueChange = { value -> onUpdateQuantity(index, value) },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                }

                TextButton(
                    onClick = { onAddNewDate() },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Adicionar Nova Data de Validade")
                }

                Button(
                    onClick = { onAddItemHelper() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Adicionar Item à Doação")
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )

        Text(
            text = "Itens na Doação",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            uiState.donationHelper.forEach { helper ->
                ItemsInBag(
                    itemName = helper.item.name,
                    totalQuantity = helper.quantity.toString(),
                    onRemove = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onClickCreate() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA)
        ) {
            Text("Concluir Doação")
        }
    }
}

@Composable
fun ItemsInBag(
    itemName: String,
    totalQuantity: String,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "$totalQuantity unidades totais",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = Color.Gray.copy(alpha = 0.5f)
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCreateDonation() {
    val mockCampaigns = listOf(
        com.ipca.socialstore.data.models.CampaignModel(
            id = 1, name = "Banco Alimentar", description = "Recolha Mensal", category = "Alimentação",
            onGoing = true, goal = 1000, currentDonations = 100, startDate = "01/01/2026", endDate = "31/01/2026"
        )
    )

    // Criar a lista de itens temporários para a "Bag"
    val mockCreateDonationHelper = listOf(
        CreateDonationHelperModel(
            item = com.ipca.socialstore.data.models.ItemModel(
                name = "Arroz Agulha",
                barCode = "560123456789", // Adicionado o barCode conforme o teu Model
                itemType = "Alimentação"
            ),
            quantity = 10,
            expirationDate = "31/12/2026"
        ),
        CreateDonationHelperModel(
            item = com.ipca.socialstore.data.models.ItemModel(
                name = "Leite Meio Gordo",
                barCode = null, // BarCode pode ser null conforme o teu Model
                itemType = "Alimentação"
            ),
            quantity = 24,
            expirationDate = "15/05/2026"
        )
    )

    val mockState = DonationState(
        donorName = "Maria Silva",
        donationDate = "09/01/2026",
        itemName = "Azeite",
        itemType = "Alimentação",
        campaigns = mockCampaigns,
        donationHelper = mockCreateDonationHelper,
        listDate = listOf(
            com.ipca.socialstore.presentation.views.item.ExpirationDate("20/10/2027", "2")
        )
    )

    SocialStoreTheme {
        androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.background) {
            CreateDonationViewContent(
                modifier = Modifier.fillMaxSize(),
                uiState = mockState,
                onUpdateDate = {},
                onUpdateName = {},
                onUpdateItemType = {},
                onUpdateQuantity = { _, _ -> },
                onUpdateExpiration = { _, _ -> },
                onClickCreate = { },
                onAddNewDate = {},
                onAddItemHelper = {},
                onUpdateDonor = {},
                onSearchItem = {}
            )
        }
    }
}


