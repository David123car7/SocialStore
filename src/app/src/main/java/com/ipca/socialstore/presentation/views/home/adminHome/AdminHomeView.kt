package com.ipca.socialstore.presentation.views.home.adminHome

import DashboardMenuItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.presentation.ui.SocialStoreScaffold
import com.ipca.socialstore.presentation.ui.SocialStoreScaffoldContent
import com.ipca.socialstore.presentation.ui.components.DashboardCard
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

data class DashboardMenuItem(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val alertCount: Int = 0
)

@Composable
fun AdminHomeView(modifier: Modifier){
    AdminHomeContent(modifier = modifier)
}
@Composable
fun AdminHomeContent(modifier: Modifier){
    val menuItems = listOf(
        DashboardMenuItem(1, "Candidaturas", Icons.Default.Star, Color(0xFFE3F2FD), alertCount = 5),
        DashboardMenuItem(2, "Gestão Stock", Icons.Default.Star, Color(0xFFFFF3E0), alertCount = 3),
        DashboardMenuItem(3, "Agendamentos", Icons.Default.DateRange, Color(0xFFF3E5F5), alertCount = 1),
        DashboardMenuItem(4, "Relatórios", Icons.Default.Star, Color(0xFFE8F5E9)),
        DashboardMenuItem(5, "Beneficiários", Icons.Default.Star, Color(0xFFE0F7FA)),
        DashboardMenuItem(6, "Definições", Icons.Default.Settings, Color(0xFFF5F5F5))
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Text("Acesso Rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(menuItems) { item ->
            DashboardCard(title = item.title, icon = item.icon, color = item.color, alertCount = item.alertCount)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminHomePreview(){
    SocialStoreTheme() {
        AdminHomeContent(modifier = Modifier)
    }
}

