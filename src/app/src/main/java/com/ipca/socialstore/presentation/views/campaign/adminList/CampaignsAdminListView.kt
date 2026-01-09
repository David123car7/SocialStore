package com.ipca.socialstore.presentation.views.campaigns

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
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
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.components.AlertComponent
import com.ipca.socialstore.presentation.ui.components.IntroductionComponent
import com.ipca.socialstore.presentation.ui.components.SearchBarContent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.IconTint
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.views.campaign.adminList.CampaignsAdminListState
import com.ipca.socialstore.presentation.views.campaign.adminList.CampaignsListState
import com.ipca.socialstore.presentation.views.campaign.adminList.CampaignsAdminListViewModel

@Composable
fun CampaignsAdminListView(
    modifier: Modifier = Modifier,
    navController: NavController,
    userRole: UserRole
) {
    val campaignsViewModel: CampaignsAdminListViewModel = hiltViewModel()
    val uiState by campaignsViewModel.uiState

    CampaignsAdminListContent(
        modifier = modifier,
        uiState = uiState,
        onCreateClick = {
            NavigationLogic.navigateTo(
                navController = navController,
                route = AdminRoutes.CreateCampaign,
                userRole = userRole
            )
        },
        onItemClick = { campaign ->

        },
        onEditClick = { campaign ->
            val routeName = AdminRoutes.CampaignEdit::class.qualifiedName!!
            navController.navigate("$routeName/${campaign.id}")
        },
        onDeleteConfirm = { campaign ->
            campaignsViewModel.deleteCampaign(campaign.id!!)
        }
    )

    LaunchedEffect(uiState.campaignDeleted) {
        if(uiState.campaignDeleted){
            campaignsViewModel.getAllCampaigns()
            campaignsViewModel.updateCampaignDeleted(false)
        }
    }
}

@Composable
fun CampaignsAdminListContent(
    modifier: Modifier,
    uiState: CampaignsAdminListState,
    onCreateClick: () -> Unit,
    onItemClick: (CampaignModel) -> Unit,
    onEditClick: (CampaignModel) -> Unit,
    onDeleteConfirm: (CampaignModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var campaignToDelete by remember { mutableStateOf<CampaignModel?>(null) }

    if (campaignToDelete != null) {
        AlertComponent(
            show = showDeleteDialog,
            title = "Eliminar Campanha",
            icon = Icons.Default.Warning,
            color = Color(0xFFFF5252),
            message = "Tens a certeza que queres eliminar o ficheiro?",
            onConfirm = {onDeleteConfirm(campaignToDelete!!)},
            onDismiss = {showDeleteDialog = false}
        )
    }

    val filteredList = remember(uiState.campaigns, searchQuery) {
        uiState.campaigns.filter { campaign ->
            campaign.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = modifier.fillMaxSize().padding(top = 18.dp, bottom = 18.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            IntroductionComponent(
                tittle = "Candidaturas"
            )

            SearchBarContent(
                modifier = Modifier.padding(15.dp),
                onSearchItem = { query -> searchQuery = query }
            )

            //HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
                        CampaignAdminItemCard(
                            modifier = Modifier.padding(15.dp),
                            campaign = campaign,
                            onClick = { onItemClick(campaign) },
                            onEditClick = { onEditClick(campaign) },
                            onDeleteClick = {
                                campaignToDelete = campaign
                                showDeleteDialog = true
                            }
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

        FloatingActionButton(
            onClick = onCreateClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = GreenIPCA,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nova Campanha")
        }
    }
}

@Composable
fun CampaignAdminItemCard(
    modifier: Modifier = Modifier,
    campaign: CampaignModel,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isActive = campaign.onGoing

    val statusText = if (isActive) "A Decorrer" else "Terminada"
    val statusColor = if (isActive) GreenIPCA else Color.Gray

    val progress = if (campaign.goal > 0) {
        (campaign.currentDonations.toFloat() / campaign.goal.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GreenIPCA),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        label = {
                            Text(
                                text = campaign.category,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium // Um pouco de peso ajuda na leitura em fundos claros
                            )
                        },
                        modifier = Modifier.height(26.dp),
                        enabled = false,
                        // 1. Remove a borda cinzenta padrão para ficar limpo
                        border = null,
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            // 2. Fundo suave (20% a 25% é o ideal para não "berrar")
                            disabledContainerColor = GreenIPCA.copy(alpha = 0.2f),

                            // 3. O TRUQUE: O texto tem de ser a cor SÓLIDA para haver contraste e ficar bonito
                            disabledLabelColor = GreenIPCA
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
                maxLines = 2,
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
                        color = statusColor
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = statusColor,
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
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${campaign.startDate} - ${campaign.endDate}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = IconTint
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}