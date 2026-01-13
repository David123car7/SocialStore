package com.ipca.socialstore.presentation.ui.notifications

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic

data class NotificationItem(
    val title: String,
    val body: String,
    val time: String
)

@Composable
fun NotificationsView(navController: NavController, userRole: UserRole){
    val viewModel: NotificationsViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(key1 = userRole) {
        Log.d("App Debug", "Nossa senhora")
        viewModel.getNotifications(userRole = userRole)
    }
    NotificationsContent(
        uiState = uiState,
        onClickHistory = {
                NavigationLogic.navigateTo(
                    navController = navController,
                    userRole = userRole,
                    route = GeneralRoutes.NotificationHistory
                )
            },
        onMarkNotificationRead = { id ->
            viewModel.markNotificationAsRead(id = id)
        },
        onMarkAllRead = {
            viewModel.markAllNotificationsAsRead()
        }
    )
}

@Composable
fun NotificationsContent(
    uiState: NotificationsState,
    onMarkAllRead:() -> Unit,
    onMarkNotificationRead:(Int) -> Unit,
    onClickHistory:() -> Unit) {
   var showNotifications by remember { mutableStateOf(false) }

    Box {
        // Ícone do Sino com "Badge" opcional (se quiseres mostrar bolinha vermelha)
        IconButton(onClick = { showNotifications = true }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificações",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = showNotifications,
            onDismissRequest = { showNotifications = false },
            modifier = Modifier
                .width(320.dp) // Um pouco mais largo para caberem os botões
                .heightIn(max = 400.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notificações",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (uiState.notifications.isNotEmpty()) {
                    TextButton(
                        onClick = onMarkAllRead,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            "Ler todas",
                            style = MaterialTheme.typography.labelMedium,
                            color = GreenIPCA
                        )
                    }
                }
            }

            HorizontalDivider()

            if (uiState.notifications.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Sem novas notificações",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    },
                    onClick = { /* Nada acontece */ }
                )
            } else {
                uiState.notifications.forEach { note ->
                    DropdownMenuItem(
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = note.tittle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = note.created_at,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = note.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { onMarkNotificationRead(note.id!!)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check, // Ícone de "Visto"
                                    contentDescription = "Marcar como lida",
                                    tint = GreenIPCA, // A tua cor
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        onClick = {
                            showNotifications = false
                        }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                }
            }

            DropdownMenuItem(
                text = {
                    Text(
                        "Ver histórico completo",
                        color = GreenIPCA,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                onClick = {
                    showNotifications = false
                    onClickHistory()
                }
            )
        }
    }
}