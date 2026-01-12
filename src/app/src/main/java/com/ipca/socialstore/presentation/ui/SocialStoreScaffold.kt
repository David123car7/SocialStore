package com.ipca.socialstore.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ipca.socialstore.data.enums.UserRole
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.CandidateRoutes
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.R
import com.ipca.socialstore.presentation.ui.notifications.NotificationItem
import com.ipca.socialstore.presentation.ui.notifications.NotificationsContent
import com.ipca.socialstore.presentation.ui.notifications.NotificationsView

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
            icon = Icons.Outlined.Home,
            route = GeneralRoutes.Home,
            isVisible = userRole == UserRole.GUEST || userRole == UserRole.DEFAULT
        ),
        BottomNavItem( //AdminHome
            icon = Icons.Outlined.Home,
            route = AdminRoutes.Home,
            isVisible = userRole == UserRole.ADMIN
        ),
        BottomNavItem( //ApplicationInfo
            icon = Icons.Outlined.FileOpen,
            route = DefaultRoutes.ApplicationInfo,
            isVisible = userRole == UserRole.DEFAULT
        ),
        BottomNavItem(
            icon = Icons.Outlined.Campaign,
            route = GeneralRoutes.CampaignsList,
            isVisible = userRole == UserRole.DEFAULT || userRole == UserRole.GUEST
        ),
        BottomNavItem(
            icon = Icons.Filled.FileOpen,
            route = CandidateRoutes.ApplicationState,
            isVisible = userRole == UserRole.CANDIDATE
        ),
        BottomNavItem(
            icon = Icons.Outlined.Assignment,
            route = AdminRoutes.SchedulingManagement,
            isVisible = userRole == UserRole.ADMIN
        ),
        BottomNavItem(
            icon = Icons.Outlined.Inventory2,
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
            route = BeneficiaryRoutes.Home,
            isVisible = userRole == UserRole.BENEFICIARY
        ),
        BottomNavItem(
            icon = Icons.Default.Schedule,
            route = BeneficiaryRoutes.Scheduling::class.qualifiedName!!,
            isVisible = userRole == UserRole.BENEFICIARY
        ),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = GreenIPCA,
                titleContentColor = Color.White,
            ),
                navigationIcon = {
                    if (userRole != UserRole.GUEST) {
                        TextButton(onClick = {
                            logout()
                        }) {
                            Text("Sair", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.saslogo),
                        contentDescription = "SASLogo",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .height(50.dp)
                            .padding(vertical = 2.dp)
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
                        }) { Text("Entrar", fontWeight = FontWeight.Bold, color = Color.White)}
                    }
                    else {
                        var showNotifications by remember { mutableStateOf(false) }

                        // Mock data for demonstration (Replace with your ViewModel data)
                        val notifications = listOf(
                            NotificationItem(
                                "Pedido Aceito",
                                "O seu pedido #123 foi aprovado.",
                                "10 min"
                            ),
                            NotificationItem("Novo Evento", "Workshop de Android amanhã.", "1h atrás"),
                            NotificationItem("Alerta", "A sua sessão vai expirar em breve.", "2h atrás")
                        )

                        // BOX is crucial here to anchor the menu to the Icon
                        Box {
                            IconButton(onClick = { showNotifications = true }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificações",
                                    tint = Color.White
                                )
                            }

                            NotificationsView(navController = navController, userRole = userRole)
                        }
                    }
                }
            )
        },


        bottomBar = {
            if (userRole != UserRole.GUEST) {
                NavigationBar {
                    allNavItems.forEach { item ->
                        if (item.isVisible) {
                            val isSelected = currentRoute?.route == item.route.toString() ||
                                    currentRoute?.route?.contains(item.route.toString()) == true

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null
                                    )
                                },
                                label = { },
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        if (item.route is String) {
                                            navController.navigate(item.route)
                                        } else {
                                            NavigationLogic.navigateTo(
                                                navController = navController,
                                                userRole = userRole,
                                                route = item.route
                                            )
                                        }
                                    }
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
            userRole = UserRole.ADMIN,
            logout = {}
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Conteúdo da Página Home (Guest)")
            }
        }
    }
}
