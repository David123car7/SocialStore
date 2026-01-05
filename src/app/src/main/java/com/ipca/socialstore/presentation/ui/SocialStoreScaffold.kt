package com.ipca.socialstore.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ipca.socialstore.data.enums.UserRole
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.CandidateRoutes
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

data class BottomNavItem(
    val icon: ImageVector,
    val route: Any,
    val isVisible: Boolean
)

@Composable
fun SocialStoreScaffold(navController: NavController, userRole: UserRole, content: @Composable (PaddingValues) -> Unit){
    val viewModel: SocialStoreScaffoldViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    SocialStoreScaffoldContent(
        navController = navController,
        userRole = userRole,
        logout = {viewModel.logout()},
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialStoreScaffoldContent(
    navController: NavController,
    userRole: UserRole,
    logout:() -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    val allNavItems = listOf(
        BottomNavItem( //Home
            icon = Icons.Default.Home,
            route = GeneralRoutes.Home,
            isVisible = userRole == UserRole.GUEST || userRole == UserRole.DEFAULT
        ),
        BottomNavItem( //AdminHome
            icon = Icons.Default.Home,
            route = AdminRoutes.Home,
            isVisible = userRole == UserRole.ADMIN
        ),
        BottomNavItem( //ApplicationInfo
            icon = Icons.Default.FileOpen,
            route = DefaultRoutes.ApplicationInfo,
            isVisible = userRole == UserRole.DEFAULT
        ),
        BottomNavItem(
            icon = Icons.Filled.FileOpen,
            route = CandidateRoutes.ApplicationState,
            isVisible = userRole == UserRole.CANDIDATE
        ),
        BottomNavItem(
            icon = Icons.Default.Accessibility,
            route = AdminRoutes.ListApplications,
            isVisible = userRole == UserRole.ADMIN
        ),
        BottomNavItem(
            icon = Icons.Default.Assignment,
            route = AdminRoutes.SchedulingManagement,
            isVisible = userRole == UserRole.ADMIN
        ),
        BottomNavItem( //stock
            icon = Icons.Default.Storage,
            route = AdminRoutes.GetStock,
            isVisible = userRole == UserRole.ADMIN
        ),
        BottomNavItem( //stock
            icon = Icons.Default.Schedule,
            route = AdminRoutes.SchedulingMainPage,
            isVisible = userRole == UserRole.ADMIN
        ),
        BottomNavItem( //stock
            icon = Icons.Default.Home,
            route = BeneficiaryRoutes.BeneficiaryHome,
            isVisible = userRole == UserRole.BENEFICIARY
        ),
        BottomNavItem( //stock
            icon = Icons.Default.Schedule,
            route = BeneficiaryRoutes.BeneficiaryScheduling,
            isVisible = userRole == UserRole.BENEFICIARY
        ),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (userRole != UserRole.GUEST) {
                        TextButton(onClick = {
                            logout()
                        }) {
                            Text("Sair", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                title = {
                    Text(
                        "Social Store",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    if (userRole == UserRole.GUEST) {
                        TextButton(onClick = {
                            NavigationLogic.navigateTo(
                                navController = navController,
                                userRole = userRole,
                                route = GeneralRoutes.Login
                            )
                        }) { Text("Entrar", fontWeight = FontWeight.Bold)}
                    }
                    else {
                        if (userRole == UserRole.ADMIN){
                            IconButton(onClick = {navController.navigate(AdminRoutes.NotificationHistory)}) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificações",
                                    tint = Color.Gray
                                )
                            }
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },

        bottomBar = {
            if(userRole != UserRole.GUEST){
                NavigationBar() {
                    allNavItems.forEach { item ->
                        if(item.isVisible){
                            NavigationBarItem(
                                icon = {Icon(imageVector = item.icon, contentDescription = "")},
                                selected = currentRoute == GeneralRoutes.Home,
                                onClick = {
                                    NavigationLogic.navigateTo(
                                        navController = navController,
                                        userRole = userRole,
                                        route = item.route
                                    )
                                }
                            )
                        }
                    }
                }
            }
            if (userRole == UserRole.BENEFICIARY){
                NavigationBar() {
                    allNavItems.forEach { item ->
                        if (item.isVisible) {
                            NavigationBarItem(
                                icon = { Icon(imageVector = item.icon, contentDescription = "") },
                                selected = currentRoute == BeneficiaryRoutes.BeneficiaryHome,
                                onClick = {
                                    NavigationLogic.navigateTo(
                                        navController = navController,
                                        userRole = userRole,
                                        route = item.route
                                    )
                                }
                            )
                        }
                    }
                }
            }

        }


    ) { paddingValues ->
        content(paddingValues)
    }
}

@Preview(showBackground = true, name = "Scaffold - Visitante")
@Composable
fun SocialStoreScaffoldGuestPreview() {
    SocialStoreTheme {
        SocialStoreScaffoldContent(
            navController = rememberNavController(),
            userRole = UserRole.BENEFICIARY,
            logout = {}
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Conteúdo da Página Home (Guest)")
            }
        }
    }
}
