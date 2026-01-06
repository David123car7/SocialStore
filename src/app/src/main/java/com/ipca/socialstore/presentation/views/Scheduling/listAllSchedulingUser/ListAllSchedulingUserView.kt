package com.ipca.socialstore.presentation.views.Scheduling.listAllSchedulingUser


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes


@Composable
fun ListAllSchedulingUserView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : ListAllSchedulingUserViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchInfo()
    }
    ListAllSchedulingUserContent(
        modifier = modifier,
        uiState = uiState,
        navController = navController,
        onClickAccept = {viewModel.selectListAccept()},
        onClickHistory = {viewModel.selectListHistory()},
        onClickCancel = {viewModel.selectListCanceled()},
        onClickProgress = {viewModel.selectListInProgress()}
    )
}
@Composable
fun ListAllSchedulingUserContent(
    modifier: Modifier,
    navController : NavController,
    uiState : ListSchedulingUserState,
    onClickAccept : () -> Unit,
    onClickHistory : () -> Unit,
    onClickCancel : () -> Unit,
    onClickProgress: () -> Unit,
){
    var selectedTab by remember { mutableStateOf("proximos") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = uiState.beneficiary?.name ?: "Carregando...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Processo nº ${uiState.beneficiary?.id ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Chip: Próximos
            FilterChip(
                selected = selectedTab == "proximos",
                onClick = {
                    selectedTab = "proximos"
                    onClickAccept()
                },
                label = { Text("Próximos (${uiState.accept})") },
                leadingIcon = if (selectedTab == "proximos") {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )


            FilterChip(
                selected = selectedTab == "historico",
                onClick = {
                    selectedTab = "historico"
                    onClickHistory()
                },
                label = { Text("Histórico") }
            )


            FilterChip(
                selected = selectedTab == "cancelado",
                onClick = {
                    selectedTab = "cancelado"
                    onClickCancel()
                },
                label = { Text("Cancelados (${uiState.cancel})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFEBEE),
                    selectedLabelColor = Color(0xFFC62828)
                )
            )

            FilterChip(
                selected = selectedTab == "pendentes",
                onClick = {
                    selectedTab = "pendentes"
                    onClickProgress()
                },
                label = { Text("Por Confirmar") }
            )
        }

        Text(
            text = "Cronograma",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(uiState.showList) { index, item ->
                SchedulingTimelineItem(
                    item = item,
                    isLast = index == uiState.scheduling.lastIndex,
                    onNavigate = {item ->
                        if (item.state == "accept" || item.state == "in_Progress") {
                            val routeName = BeneficiaryRoutes.SchedulingConfirmation::class.qualifiedName
                            navController.navigate("$routeName/${item.id}")
                        }
                        if (item.state == "canceled" || item.state == "justified"){
                            val routeName = BeneficiaryRoutes.JustifyScheduling::class.qualifiedName
                            navController.navigate("$routeName/${item.id}")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SchedulingTimelineItem(
    item: SchedulingModel,
    isLast: Boolean,
    onNavigate: (SchedulingModel) -> Unit,
) {

    val isAccept = item.state == "accept"
    val statusColor = if (isAccept) Color(0xFF4CAF50) else Color(0xFFEF5350)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {

        Card(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .clickable(
                    onClick = {onNavigate(item)}),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.schedulingDate,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (isAccept) "Entrega de Agendada" else "Entrega Cancelada",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                val (label, containerColor, contentColor) = when (item.state) {
                    "accept" -> Triple("Confirmado", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    "justified" -> Triple("Justificada", Color(0xFFE3F2FD), Color(0xFF1976D2))
                    "in_Progress" -> Triple("Por Confirmar", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                    else -> Triple("Justificar", Color(0xFFFFEBEE), Color(0xFFD32F2F))
                }
                Surface(
                    color = containerColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = contentColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListAllSchedulingUserPreview() {
    val mockBeneficiary = BeneficiaryModel(
        id = 3,
        name = "David",
        birthDate = "1990-05-15",
        academicId = 1,
        phoneNumber = "954525458"

    )

    val mockScheduling = listOf(
        SchedulingModel(id = 6, schedulingDate = "2026-01-24", beneficiaryId = 3, state = "accept", reason = null, null),
        SchedulingModel(id = 2, schedulingDate = "2026-01-22", beneficiaryId = 3, state = "canceled", reason = null,null),
        SchedulingModel(id = 4, schedulingDate = "2026-01-14", beneficiaryId = 3, state = "canceled", reason = null, null)
    )

    val mockUiState = ListSchedulingUserState(
        beneficiary = mockBeneficiary,
        scheduling = mockScheduling,
        accept = 1,
        cancel = 2
    )

    MaterialTheme {
        ListAllSchedulingUserContent(
            modifier = Modifier,
            uiState = mockUiState,
            navController = rememberNavController(),
            onClickHistory = {},
            onClickAccept = {},
            onClickCancel = {},
            onClickProgress = {}
        )
    }
}

