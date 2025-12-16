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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.presentation.objects.NavigationLogic
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

sealed class BottomNavItem(val route: Any, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(GeneralRoutes.ResetView, Icons.Default.Home, "Início")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialStoreScaffold(
    navController: NavController,
    userRole: UserRole,
    logout:() -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomItems = listOf(
        BottomNavItem.Home,
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    if (userRole != UserRole.NOROLE) {
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
                    if (userRole == UserRole.NOROLE) {
                        TextButton(onClick = {
                            NavigationLogic.navigateTo(
                                navController = navController,
                                userRole = userRole,
                                route = GeneralRoutes.Login
                            )}
                        ) { Text("Entrar", fontWeight = FontWeight.Bold)}
                    }
                    else {
                        IconButton(onClick = { /* Navegar para Perfil */ }) {
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
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
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
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Preview(showBackground = true, name = "Scaffold - Visitante")
@Composable
fun SocialStoreScaffoldGuestPreview() {
    SocialStoreTheme {
        SocialStoreScaffold(
            navController = rememberNavController(),
            userRole = UserRole.DEFAULT,
            logout = {}
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Conteúdo da Página Home (Guest)")
            }
        }
    }
}
