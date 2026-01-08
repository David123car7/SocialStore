package com.ipca.socialstore.presentation.views.donation.listAllDonations

import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.models.DonationHelperModel
import com.ipca.socialstore.presentation.routes.AdminRoutes


@Composable
fun ListAllDonationsView(
    modifier: Modifier,
    navController: NavController
){

    val viewModel : ListAllDonationsViewModel = hiltViewModel()
    val uiState by viewModel.uiState


    ListAllDonationsContent(
        modifier = modifier,
        uiState = uiState,
        navController = navController,
        onFilterCampaign = {value -> viewModel.filterDonationsByCampaign(value)}
    )
}

@Composable
fun ListAllDonationsContent(
    modifier: Modifier,
    uiState: ListDonationsState,
    navController: NavController,
    onFilterCampaign: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCampaignName by remember { mutableStateOf("Selecionar Campanha") }
    val listToDisplay = uiState.filterDonations ?: uiState.joinedDonations
    var showPopUp by remember { mutableStateOf(false) }
    var selectedDonation by remember { mutableStateOf<DonationHelperModel?>(null) }

    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                            onFilterCampaign(-1)
                            expanded = false
                        }
                    )

                    HorizontalDivider()
                    uiState.campaigns?.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = {
                                item.id?.let { safeId ->
                                    selectedCampaignName = item.name
                                    onFilterCampaign(safeId)
                                    expanded = false
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listToDisplay) { donation ->
                    DonationCard(
                        campaignName = donation.campaignName,
                        date = donation.donation.date,
                        donorName = donation.donation.donorName,
                        onClick = {
                            selectedDonation = donation
                            showPopUp = true
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(AdminRoutes.CreateDonation)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Criar Doação")
        }
    }

    if (showPopUp && selectedDonation != null){
        DonationDetailSheet(
            donationHelper = selectedDonation!!,
            onDismiss = {showPopUp = false}
        )
    }
}


@Composable
fun DonationCard(
    campaignName: String,
    donorName : String,
    date: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Doador: $donorName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF136342),
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Campanha: $campaignName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF136342), // Verde Social Store
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationDetailSheet(
    donationHelper: DonationHelperModel,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp) // Espaço extra no fundo
        ) {
            // Título e Badge de Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumo da Doação",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFF136342).copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Concluída",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF136342)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Informações principais em Cards pequenos ou linhas
            DetailInfoRow(Icons.Default.Person, "Doador", donationHelper.donation.donorName)
            DetailInfoRow(Icons.Default.Campaign, "Campanha", donationHelper.campaignName)
            DetailInfoRow(Icons.Default.Event, "Data da Entrega", donationHelper.donation.date)

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp)

            // Secção de Itens
            Text(
                text = "Itens Doados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Tipo item" ?: "Nenhum detalhe adicional fornecido sobre os itens.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF136342), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

