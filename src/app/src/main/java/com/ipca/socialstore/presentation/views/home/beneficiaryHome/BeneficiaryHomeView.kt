package com.ipca.socialstore.presentation.views.home.beneficiaryHome

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.models.NotificationReceiverModel
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.components.DashboardCard
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.views.home.adminHome.ActivityListSection
import com.ipca.socialstore.presentation.views.home.adminHome.ActivityRow
import com.ipca.socialstore.presentation.views.home.adminHome.DashboardMenuItem
import com.ipca.socialstore.presentation.views.home.adminHome.getIconForType

data class BeneficiaryMenuItem(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun BeneficiaryHomeView(modifier: Modifier, navController: NavController,userRole : UserRole){
    val viewModel: BeneficiaryHomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    val context = LocalContext.current

    BenificiaryHomeContent(
        modifier = modifier,
        uiState = uiState,
        onClickDocuments = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = BeneficiaryRoutes.Documents
            )
        },
        onClickProfile = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = BeneficiaryRoutes.Profile
            )
        },
        onClickCampaign = {

        },
        onClickSchedule = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = BeneficiaryRoutes.Scheduling::class.qualifiedName!!
            )
        }
    )
}

@Composable
    fun BenificiaryHomeContent(
    modifier: Modifier,
    uiState: BeneficiaryHomeState,
    onClickDocuments:() -> Unit,
    onClickProfile:() -> Unit,
    onClickCampaign:() -> Unit,
    onClickSchedule:() -> Unit){
        val menuItems = listOf(
            BeneficiaryMenuItem(1, "Documentos", Icons.Outlined.Add, onClick = onClickDocuments),
            BeneficiaryMenuItem(2, "Dados Pessoais", Icons.Outlined.Person, onClick = onClickProfile),
            BeneficiaryMenuItem(3, "Campanhas", Icons.Outlined.Campaign, onClick = onClickCampaign),
            BeneficiaryMenuItem(4, "Agendamentos", Icons.Outlined.Schedule, onClick = onClickSchedule),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(bottom = 8.dp))
                {
                    Text(
                        text = uiState.beneficiary.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Bem-vindo de volta!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Acesso Rápido",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            items(menuItems) { item ->
                DashboardCard(
                    title = item.title,
                    icon = item.icon,
                    onClick = item.onClick
                )
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Últimas Atividades",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            item(span = { GridItemSpan(2) }) {
                ActivityListSection(notifications = uiState.notifications)
            }
        }
    }

@Composable
fun ActivityListSection(
    notifications: List<NotificationReceiverModel>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F2F4)), // Light Gray
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (notifications.isEmpty()) {
                Text(
                    text = "Nenhuma atividade recente",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Gray
                )
            } else {
                notifications.forEachIndexed { index, notification ->
                    ActivityRow(
                        icon = getIconForType(notification.type),
                        title = notification.tittle, // Ensure your model has 'title' corrected from 'tittle'
                        time = notification.created_at // You may want to parse/format this date string
                    )

                    // Add Divider only if it is NOT the last item
                    if (index < notifications.size - 1) {
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BenificiaryHomePreview(){
    SocialStoreTheme() {
        //BenificiaryHomeContent(modifier = Modifier, onClickProfile = {}, onClickDocuments = {})
    }
}

