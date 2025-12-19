package com.ipca.socialstore.presentation.views.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.ui.components.NotificationLogCardComponent
import com.ipca.socialstore.presentation.views.mockups.ActiveCard


@Composable
fun NotificationHistoryView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : NotificationViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getAllNotification()
    }

    NotificationHistoryContent(
        modifier = modifier,
        uiState = uiState
    )
}

@Composable
fun NotificationHistoryContent(
    modifier: Modifier = Modifier,
    uiState: NotificationState
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            contentPadding = PaddingValues(top = 15.dp, bottom = 15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Usar um fallback para lista vazia se for null
            val notifications = uiState.notification ?: emptyList()

            if (notifications.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Sem notificações no histórico", color = Color.Gray)
                    }
                }
            } else {
                items(notifications) { notification ->
                    NotificationLogCardComponent(
                        // 2. Usar ?: para fornecer textos padrão em vez de !!
                        title = notification.title ?: "Alerta",
                        message = notification.subject ?: "Sem detalhes disponíveis",
                        date = notification.createdAt ?: "",
                        icon = Icons.Default.Inventory2,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}