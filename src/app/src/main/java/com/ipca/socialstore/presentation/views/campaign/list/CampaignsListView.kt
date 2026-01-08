package com.ipca.socialstore.presentation.views.campaign.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.presentation.ui.components.SearchBarContent
import com.ipca.socialstore.presentation.views.campaign.adminList.CampaignsListState
import com.ipca.socialstore.presentation.views.campaign.adminList.CampaignsListViewModel
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.ui.tooling.preview.Preview
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.IconBgColor
import com.ipca.socialstore.presentation.ui.theme.IconTint

@Composable
fun CampaignsListView(
    modifier: Modifier = Modifier,
) {
    val campaignsViewModel: CampaignsListViewModel = hiltViewModel()
    val uiState by campaignsViewModel.uiState

    CampaignsListContent(
        modifier = modifier,
        uiState = uiState,
    )
}

@Composable
fun CampaignsListContent(
    modifier: Modifier = Modifier,
    uiState: CampaignsListState,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(uiState.campaigns, searchQuery) {
        uiState.campaigns.filter { campaign ->
            campaign.name.contains(searchQuery, ignoreCase = true)
        }
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
            Text(
                text = "${filteredList.size} campanhas encontradas",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
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
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { campaign ->
                        CampaignItemCard(
                            modifier = Modifier.padding(),
                            campaign = campaign,
                        )
                    }
                }
            }
        }
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = uiState.error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun CampaignItemCard(
    modifier: Modifier = Modifier,
    campaign: CampaignModel,
) {
    val isActive = campaign.onGoing

    val containerColor = if (isActive)
        IconBgColor
    else
        IconBgColor.copy(alpha = 0.3f)

    val statusText = if (isActive) "A Decorrer" else "Terminada"
    val statusColor = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray

    val progress = if (campaign.goal > 0) {
        (campaign.currentDonations.toFloat() / campaign.goal.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
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
                        enabled = false,
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                Surface(
                    color = if(isActive) GreenIPCA else Color.Gray,
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

            Text(
                text = campaign.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progresso: ${campaign.currentDonations} / ${campaign.goal}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GreenIPCA,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = IconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${campaign.startDate} - ${campaign.endDate}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Lista de Campanhas")
@Composable
fun CampaignsListPreview() {
    // 1. Criar dados de teste (Mock Data)
    val mockCampaigns = listOf(
        CampaignModel(
            id = 1,
            name = "Recolha de Natal",
            description = "Estamos a recolher brinquedos e roupas para crianças carenciadas durante a época festiva.",
            category = "Brinquedos",
            onGoing = true, // A decorrer
            startDate = "01/12/2024",
            endDate = "25/12/2024",
            goal = 100,
            currentDonations = 65
        ),
        CampaignModel(
            id = 2,
            name = "Banco Alimentar",
            description = "Recolha de bens essenciais não perecíveis.",
            category = "Alimentação",
            onGoing = true,
            startDate = "10/01/2024",
            endDate = "20/01/2024",
            goal = 500,
            currentDonations = 120
        ),
        CampaignModel(
            id = 3,
            name = "Material Escolar 2023",
            description = "Campanha finalizada para apoio ao início do ano letivo.",
            category = "Educação",
            onGoing = false, // Terminada
            startDate = "01/09/2023",
            endDate = "15/09/2023",
            goal = 200,
            currentDonations = 200
        )
    )

    // 2. Criar o estado da UI com os dados
    val mockState = CampaignsListState(
        campaigns = mockCampaigns,
        isLoading = false,
        error = null,
    )

    // 3. Renderizar o componente com o tema
    com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme {
        CampaignsListContent(
            modifier = Modifier.fillMaxSize(),
            uiState = mockState,
        )
    }
}

@Preview(showBackground = true, name = "Lista Vazia")
@Composable
fun CampaignsListEmptyPreview() {
    val emptyState = CampaignsListState(
        campaigns = emptyList(),
        isLoading = false,
        error = null
    )

    com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme {
        CampaignsListContent(
            modifier = Modifier.fillMaxSize(),
            uiState = emptyState,
        )
    }
}