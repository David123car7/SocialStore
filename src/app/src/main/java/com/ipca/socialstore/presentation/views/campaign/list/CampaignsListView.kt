package com.ipca.socialstore.presentation.views.campaigns

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.presentation.ui.components.SearchBarContent // Assumindo que tens este componente
import com.ipca.socialstore.presentation.views.campaign.list.CampaignsListState
import com.ipca.socialstore.presentation.views.campaign.list.CampaignsListViewModel


@Composable
fun CampaignsListView(modifier: Modifier = Modifier, navController: NavController){
    val campaignsViewModel: CampaignsListViewModel = hiltViewModel()
    val uiState by campaignsViewModel.uiState

    CampaignsListContent(
        modifier = modifier,
        uiState = uiState,
        onRefresh = {},
        onCreateClick = {},
        onItemClick = {}
    )
}

@Composable
fun CampaignsListContent(
    modifier: Modifier = Modifier,
    uiState: CampaignsListState,
    onRefresh: () -> Unit,
    onCreateClick: () -> Unit,
    onItemClick: (CampaignModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categories = remember(uiState.campaigns) {
        uiState.campaigns.map { it.category }.filter { it.isNotBlank() }.distinct().sorted()
    }

    val filteredList = remember(uiState.campaigns, searchQuery, selectedCategory) {
        uiState.campaigns.filter { campaign ->
            val matchesSearch = campaign.name.contains(searchQuery, ignoreCase = true) ||
                    campaign.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || campaign.category == selectedCategory

            matchesSearch && matchesCategory
        }
    }

    LaunchedEffect(Unit) {
        onRefresh()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            SearchBarContent(
                onSearchItem = { query -> searchQuery = query }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 2. Filtros (Chips)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AssistChip(
                        onClick = { selectedCategory = null },
                        label = { Text("Todas") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedCategory == null)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = null
                    )
                }
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = if (isSelected) null else category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Text(
                text = "${filteredList.size} campanhas encontradas",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 3. Conteúdo da Lista
            if (filteredList.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Nenhuma campanha encontrada." else "Não existem campanhas.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp), // Espaço para o FAB
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { campaign ->
                        CampaignItemCard(
                            campaign = campaign,
                            onClick = { onItemClick(campaign) }
                        )
                    }
                }
            }
        }

        // 4. Estados de Loading e Erro (Sobrepostos)
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 5. FAB (Criar Nova Campanha)
        FloatingActionButton(
            onClick = onCreateClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nova Campanha")
        }
    }
}

// --- COMPONENTE DO ITEM (CARD) ---

@Composable
fun CampaignItemCard(
    modifier: Modifier = Modifier,
    campaign: CampaignModel,
    onClick: () -> Unit
) {
    // Lógica visual baseada no estado da campanha
    val isActive = campaign.onGoing

    val containerColor = if (isActive)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    else
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    val statusText = if (isActive) "A Decorrer" else "Terminada"
    val statusIcon = if (isActive) Icons.Default.EventAvailable else Icons.Default.EventBusy
    val statusColor = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabeçalho: Nome e Categoria
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = campaign.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text(campaign.category, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(26.dp),
                        enabled = false, // Apenas visual
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                // Status Badge
                Surface(
                    color = if(isActive) MaterialTheme.colorScheme.primary else Color.Gray,
                    shape = CircleShape,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Descrição (truncada)
            Text(
                text = campaign.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Rodapé: Datas
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${campaign.startDate} até ${campaign.endDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// --- PREVIEWS (Para testares sem correres a app) ---

@Preview(showBackground = true)
@Composable
fun PreviewCampaignList() {
    val mockCampaigns = listOf(
        CampaignModel(
            id = 1,
            name = "Recolha de Natal 2024",
            description = "Recolha de alimentos e brinquedos para distribuir na época natalícia.",
            category = "Sazonal",
            onGoing = true,
            startDate = "01/12/2024",
            endDate = "20/12/2024"
        ),
        CampaignModel(
            id = 2,
            name = "Volta às Aulas",
            description = "Apoio com material escolar para famílias carenciadas.",
            category = "Educação",
            onGoing = false,
            startDate = "01/09/2024",
            endDate = "30/09/2024"
        ),
        CampaignModel(
            id = 3,
            name = "Banco Alimentar de Verão",
            description = "Reforço de stock para os meses de verão.",
            category = "Alimentar",
            onGoing = true,
            startDate = "01/06/2025",
            endDate = "31/08/2025"
        )
    )

    MaterialTheme {
        val mockCampaignsList = listOf(
            CampaignModel(
                id = 1,
                name = "Campanha de Natal Solidário",
                description = "Recolha de brinquedos e roupas quentes para distribuir na véspera de Natal às famílias carenciadas do concelho.",
                category = "Sazonal",
                onGoing = true,
                startDate = "01/12/2025",
                endDate = "24/12/2025"
            ),
            CampaignModel(
                id = 2,
                name = "Kit Escolar 2025",
                description = "Angariação de cadernos, mochilas e material de escrita para o início do ano letivo.",
                category = "Educação",
                onGoing = false, // Finished
                startDate = "01/09/2025",
                endDate = "15/09/2025"
            ),
        )
        val uiState = CampaignsListState(
            campaigns = mockCampaignsList,
            error = null
        )

        CampaignsListContent(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
            onItemClick = {},
            onCreateClick = {},
            onRefresh = {},
        )
    }
}