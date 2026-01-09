package com.ipca.socialstore.presentation.views.home.adminHome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.components.DashboardCard
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic

data class DashboardMenuItem(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AdminHomeView(modifier: Modifier, navController: NavController, userRole: UserRole){
    AdminHomeContent(
        modifier = modifier,
        navigateTo = { route ->
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = route
            )
        }
    )
}
@Composable
fun AdminHomeContent(modifier: Modifier, navigateTo: (route: Any) -> Unit) {

    val menuItems = listOf(
        DashboardMenuItem(1, "Candidaturas", Icons.Outlined.Description, onClick = {navigateTo(AdminRoutes.ListApplications)}),
        DashboardMenuItem(2, "Doações", Icons.Outlined.Handshake, onClick = {navigateTo(AdminRoutes.CreateDonation)}),
        DashboardMenuItem(3, "Campanhas", Icons.Outlined.Campaign, onClick = {navigateTo(AdminRoutes.CampaignList)}),
        DashboardMenuItem(4, "Agendamentos", Icons.Outlined.AccessTime, onClick = {navigateTo(AdminRoutes.SchedulingMainPage)}),
        DashboardMenuItem(5, "Beneficiários", Icons.Outlined.People, onClick = {navigateTo(AdminRoutes.SchedulingManagement)}),
        DashboardMenuItem(6, "Definições", Icons.Outlined.Settings, onClick = {})
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
        // --- Secção 1: Título Módulos ---
        item(span = { GridItemSpan(2) }) {
            Text(
                "Módulos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(menuItems) { item ->
            DashboardCard(
                title = item.title,
                icon = item.icon,
                onClick = item.onClick
            )
        }

        // --- Secção 3: Título Atividades ---
        item(span = { GridItemSpan(2) }) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Últimas Atividades",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item(span = { GridItemSpan(2) }) {
            ActivityListSection()
        }
    }
}

@Composable
fun ActivityListSection() {
    // Container cinzento grande
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F2F4)), // Cinzento claro
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Item 1
            ActivityRow(
                icon = Icons.Outlined.Description,
                title = "João Silva submeteu candidatura",
                time = "Há 2 minutos"
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha=0.5f))

            // Item 2
            ActivityRow(
                icon = Icons.Outlined.Inventory2,
                title = "Sistema: Stock de Arroz atualizado",
                time = "Há 3 dias"
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha=0.5f))

            // Item 3
            ActivityRow(
                icon = Icons.Outlined.Description, // Ícone diferente se quiseres
                title = "Maria Costa cancelou agendamento",
                time = "Há 5 dias"
            )
        }
    }
}

@Composable
fun ActivityRow(icon: ImageVector, title: String, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Ícone pequeno com fundo verde
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFA5D6A7).copy(alpha = 0.5f)) // Mesmo verde dos cards
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF1B5E20)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Textos
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminHomePreview() {
    MaterialTheme {
        AdminHomeContent(modifier = Modifier, navigateTo = {})
    }
}