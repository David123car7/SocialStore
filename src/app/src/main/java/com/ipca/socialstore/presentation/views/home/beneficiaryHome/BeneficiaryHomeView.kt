package com.ipca.socialstore.presentation.views.home.beneficiaryHome

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
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
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.components.DashboardCard
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.views.home.adminHome.DashboardMenuItem

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
        }
    )
}

@Composable
    fun BenificiaryHomeContent(
    modifier: Modifier,
    uiState: BeneficiaryHomeState,
    onClickDocuments:() -> Unit,
    onClickProfile:() -> Unit){
        val menuItems = listOf(
            BeneficiaryMenuItem(1, "Documentos", Icons.Outlined.Add, onClick = onClickDocuments),
            BeneficiaryMenuItem(2, "Dados Pessoais", Icons.Outlined.Person, onClick = onClickProfile),
            BeneficiaryMenuItem(3, "Mensagens", Icons.Outlined.Mail, onClick = {}),
            BeneficiaryMenuItem(4, "Historico", Icons.Outlined.History, onClick = {}),
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
        }
    }

@Preview(showBackground = true)
@Composable
fun BenificiaryHomePreview(){
    SocialStoreTheme() {
        //BenificiaryHomeContent(modifier = Modifier, onClickProfile = {}, onClickDocuments = {})
    }
}

