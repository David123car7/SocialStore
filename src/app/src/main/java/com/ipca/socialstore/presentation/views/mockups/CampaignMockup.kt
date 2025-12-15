package com.ipca.socialstore.presentation.views.mockups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- MODEL ---
data class Campaign(
    val id: Int,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val endDate: String,
    val isActive: Boolean
)

// --- SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignManagementScreenMockup() {
    // Dados de exemplo
    val campaigns = remember { mutableStateListOf(
        Campaign(1, "Cabaz de Natal", 1000.0, 750.0, "24 Dez", true),
        Campaign(2, "Material Escolar", 500.0, 120.0, "15 Set", true),
        Campaign(3, "Obras na Sede", 2000.0, 2000.0, "01 Jan", false) // Já terminou
    )}

    // Estado para controlar o formulário de "Nova Campanha"
    var showCreateSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestão de Campanhas") },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Info, null) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título da Secção
            item {
                Text("Campanhas Ativas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(campaigns) { campaign ->
                CampaignAdminCard(campaign)
            }
        }

        // O Formulário de Criação (BottomSheet)
        if (showCreateSheet) {
            ModalBottomSheet(onDismissRequest = { showCreateSheet = false }) {
                CreateCampaignForm(onCancel = { showCreateSheet = false })
            }
        }
    }
}

@Composable
fun CampaignAdminCard(campaign: Campaign) {
    val progress = (campaign.currentAmount / campaign.targetAmount).toFloat()
    val isFinished = campaign.currentAmount >= campaign.targetAmount

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (campaign.isActive) Color.White else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Linha 1: Título e Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(campaign.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                // Chip de Estado
                if (campaign.isActive) {
                    AssistChip(
                        onClick = {},
                        label = { Text("ON", color = Color(0xFF2E7D32)) }, // Verde
                        leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp)) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFE8F5E9), labelColor = Color(0xFF2E7D32))
                    )
                } else {
                    Text("Fechado", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Linha 2: Progresso
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Angariado: ${campaign.currentAmount}€", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("Meta: ${campaign.targetAmount}€", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (isFinished) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(Modifier.height(16.dp))
            Divider() // Note: In newer Material3 versions, use HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Linha 3: Ações de Gestão
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* Editar */ }) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Editar")
                }
                TextButton(onClick = { /* Ver Doadores */ }) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Doadores")
                }
            }
        }
    }
}

@Composable
fun CreateCampaignForm(onCancel: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
        Text("Nova Campanha", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = "", onValueChange = {},
            label = { Text("Título da Campanha") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = "", onValueChange = {},
                label = { Text("Meta (€)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = "", onValueChange = {},
                label = { Text("Data Fim") },
                modifier = Modifier.weight(1f),
                trailingIcon = { Icon(Icons.Default.DateRange, null) }
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
            Text("Criar Campanha")
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CampaignManagementScreenPreview() {
    MaterialTheme {
        CampaignManagementScreenMockup()
    }
}