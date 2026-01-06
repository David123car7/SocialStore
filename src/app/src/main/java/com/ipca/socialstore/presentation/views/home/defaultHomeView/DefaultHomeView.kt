package com.ipca.socialstore.presentation.views.home.defaultHomeView

import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.ui.SocialStoreScaffold
import com.ipca.socialstore.presentation.ui.SocialStoreScaffoldContent
import kotlin.text.uppercase

@Composable
fun DefaultHomeView(modifier: Modifier = Modifier, navController: NavController, userRole: UserRole) {
    val homeViewModel: DefaultHomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState

    DefaultHomeViewContent(
        modifier = modifier,
        uiState = uiState,
        onClickApplication = {
            NavigationLogic.navigateTo(
                navController = navController, userRole = userRole, route = DefaultRoutes.ApplicationInfo
            )
        },
        onCampaignClick = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = GeneralRoutes.CampaignsList
            )
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultHomeViewContent(
    modifier: Modifier,
    uiState: DefaultHomeState,
    onClickApplication: () -> Unit,
    onCampaignClick: () -> Unit
) {
        Column(
            modifier = modifier
                .padding(8.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(
                space = 20.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Text(
                    "Ajudar nunca foi tão fácil.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "Descubra as campanhas a decorrer no IPCA e faça a diferença hoje.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (uiState.campaignList.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.campaignList) { campaign ->
                        PublicCampaignCard(campaign, onClick = onCampaignClick)
                    }
                }
            } else {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    EmptyCampaignsCard(onClick = {})
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("500+", "Pessoas Apoiadas")
                StatItem("12", "Bens Recolhidos")
            }

            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                NeedHelpBanner(onClick = onClickApplication)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Junte-se à causa", fontWeight = FontWeight.Bold)
                Text("SAS IPCA - Serviços de Ação Social", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }

}

@Composable
fun EmptyCampaignsCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFB74D),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, null, tint = Color.White)
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        "Sem campanhas ativas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        "Mas a Loja Social precisa sempre de si. Saiba como fazer um donativo espontâneo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100).copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp).width(200.dp)
                ) {
                    Text(
                        "Ver Mais",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PublicCampaignCard(campaign: CampaignModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(300.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.6f))))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = campaign.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = campaign.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    campaign.description,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Ver")
                }
            }
        }
    }
}

@Composable
fun NeedHelpBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Azul suave
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Precisa de apoio social?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )

                Text(
                    "Se faz parte do IPCA e encontra-se com dificuldades, submeta a sua candidatura.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1565C0).copy(0.8f)
                )

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Pedir Apoio", fontSize = 12.sp)
                }
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF1565C0).copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

private val mockCampaigns = listOf(
    CampaignModel(
        id = 1,
        name = "Natal Solidário",
        description = "Ajude-nos a compor 500 cabazes para as famílias mais carenciadas da comunidade académica.",
        category = "Alimentar",
        onGoing = true,
        startDate = "",
        endDate = "",
        goal = 100,
        currentDonations = 0    ),
    CampaignModel(
        id = 2,
        name = "Kit Escolar 2026",
        description = "Recolha de cadernos, canetas e calculadoras para o segundo semestre.",
        category = "Educação",
        onGoing = true,
        startDate = "",
        endDate = "",
        goal = 100,
        currentDonations = 0    ),
    CampaignModel(
        id = 3,
        name = "Inverno Quente",
        description = "Estamos a recolher casacos e mantas em bom estado.",
        category = "Vestuário",
        onGoing = true,
        startDate = "",
        endDate = "",
        goal = 100,
        currentDonations = 0    )
)

@Preview(showBackground = true)
@Composable
fun DefaultHomePreviewWithBars(){
    SocialStoreTheme {
        val uiState = DefaultHomeState(
            error = null,
            campaignList = emptyList(),
            isLoading = false
        )
        SocialStoreScaffoldContent(navController = rememberNavController(), userRole = UserRole.DEFAULT, logout = {}) {
            DefaultHomeViewContent(
                modifier = Modifier,
                uiState = uiState,
                onCampaignClick = {},
                onClickApplication = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultHomePreview(){
    SocialStoreTheme {
        val uiState = DefaultHomeState(
            error = null,
            campaignList = emptyList(),
            isLoading = false
        )
        DefaultHomeViewContent(
            modifier = Modifier,
            uiState = uiState,
            onCampaignClick = {},
            onClickApplication = {}
        )
    }
}