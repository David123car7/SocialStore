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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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

    // 1. Usamos um Box como raiz para permitir sobreposição (overlay)
    Box(modifier = modifier.fillMaxSize()) {

        // 2. O conteúdo principal (Filtro + Lista) fica dentro da Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Bloco do Dropdown (mantém-se igual)
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
                    // ... (itens do dropdown)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp), // Espaço extra para o FAB não tapar o último item
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listToDisplay) { donation ->
                    DonationCard(
                        campaignName = donation.campaignName,
                        date = donation.donation.date,
                        donorName = donation.donation.donorName,
                        onClick = { /* ... */ }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(AdminRoutes.CreateDonation)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Agora o align funciona!
                .padding(24.dp)
                .navigationBarsPadding(),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Criar Doação")
        }
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
                    color = Color(0xFF136342), // Verde Social Store
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

