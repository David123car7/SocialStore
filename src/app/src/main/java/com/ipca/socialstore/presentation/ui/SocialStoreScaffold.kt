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
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.CandidateRoutes
import com.ipca.socialstore.presentation.utils.NavigationLogic
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import kotlinx.coroutines.selects.select

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
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "") },
                        label = { Text("Home") },
                        selected = currentRoute == GeneralRoutes.Home,
                        onClick = {
                            NavigationLogic.navigateTo(
                                navController = navController,
                                userRole = userRole,
                                route = GeneralRoutes.Home
                            )
                        }
                    )
                    if(userRole == UserRole.DEFAULT){
                        NavigationBarItem(
                            icon = { Icon( imageVector = Icons.Default.Warning, contentDescription = "") },
                            label = { Text("Candidatura") },
                            selected = currentRoute == DefaultRoutes.ApplicationInfo,
                            onClick = {
                                NavigationLogic.navigateTo(
                                    navController = navController,
                                    userRole = userRole,
                                    route = DefaultRoutes.ApplicationInfo
                                )
                            }
                        )
                    }
                    else if(userRole == UserRole.CANDIDATE){
                        NavigationBarItem(
                            icon = { Icon( imageVector = Icons.Default.Warning, contentDescription = "") },
                            label = { Text("Candidatura") },
                            selected = currentRoute == DefaultRoutes.ApplicationInfo,
                            onClick = {
                                NavigationLogic.navigateTo(
                                    navController = navController,
                                    userRole = userRole,
                                    route = CandidateRoutes.ApplicationState
                                )
                            }
                        )
                    }
                }
            }
            if (userRole == UserRole.ADMIN){
                NavigationBar()
                {
                    NavigationBarItem(
                        icon = {Icon(imageVector = Icons.Default.Home, contentDescription = "")},
                        selected = currentRoute == GeneralRoutes.Home,
                        onClick = {
                            NavigationLogic.navigateTo(
                                navController = navController,
                                userRole = userRole,
                                route = GeneralRoutes.Home
                            )
                        }
                    )
                    NavigationBarItem(
                        icon = {Icon(imageVector = Icons.Default.FileOpen, contentDescription = "")},
                        selected = currentRoute == {/**/} ,
                        onClick = {
                            NavigationLogic.navigateTo(
                                navController = navController,
                                userRole = UserRole.ADMIN,
                                route = {/**/}
                            )
                        }
                    )
                    NavigationBarItem(
                        icon = {Icon(imageVector = Icons.Default.Storage, contentDescription = "")},
                        selected = currentRoute == AdminRoutes.GetStock,
                        onClick = {
                            NavigationLogic.navigateTo(
                                navController = navController,
                                userRole = UserRole.ADMIN,
                                route = AdminRoutes.GetStock
                            )
                        }
                    )
                    NavigationBarItem(
                        icon = {Icon(imageVector = Icons.Default.Notifications, contentDescription = "")},
                        selected = currentRoute == AdminRoutes.NotificationHistory,
                        onClick = {
                            NavigationLogic.navigateTo(
                                navController = navController,
                                userRole = UserRole.ADMIN,
                                route = AdminRoutes.NotificationHistory
                            )
                        }
                    )
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
