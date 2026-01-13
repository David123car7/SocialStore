package com.ipca.socialstore.presentation.views.Scheduling.management

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.components.IntroductionComponent
import com.ipca.socialstore.presentation.ui.components.SearchBarContent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA

@Composable
fun SchedulingManagementView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : SchedulingManagementViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    SchedulingManagementContent(
        modifier = modifier,
        uiState = uiState,
        onSearchBeneficiary = { value -> viewModel.onSearchBeneficiary(value) },
        onOpenDialog = { beneficiary -> viewModel.openDecisionDialog(beneficiary) },
        onConfirmForgive = { viewModel.forgiveBeneficiary() },
        onDismissDialog = { viewModel.dismissDialog() },
        onConfirmSuspension = { viewModel.confirmSuspension() },
        onClick = { item ->
            val routeName = AdminRoutes.BeneficiaryManagement::class.qualifiedName!!
            navController.navigate("$routeName/${item.id}")
        }
    )
}

@Composable
fun SchedulingManagementContent(
    modifier: Modifier,
    uiState: SchedulingManagementState,
    onSearchBeneficiary: (value: String) -> Unit,
    onClick: (BeneficiaryModel) -> Unit,
    onOpenDialog: (BeneficiaryModel) -> Unit,
    onConfirmForgive: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmSuspension: () -> Unit
) {
    if (uiState.showDecisionDialog && uiState.selectedBeneficiary != null) {
        SuspensionDecisionDialog(
            beneficiaryName = uiState.selectedBeneficiary.name,
            onConfirmSuspension = {
                onConfirmSuspension()
                onDismissDialog()
            },
            onForgive = { onConfirmForgive() },
            onDismiss = { onDismissDialog() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(top = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IntroductionComponent(tittle = "Beneficiarios")

        SearchBarContent(
            modifier = Modifier.padding(15.dp),
            onSearchItem = { value -> onSearchBeneficiary(value) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lista de Beneficiários",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        val beneficiaries = uiState.filteredBeneficiaries ?: emptyList()

        if (beneficiaries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Nenhum beneficiário encontrado", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(beneficiaries) { _, item ->
                    val missedCount = item.missedAppointments ?: 0
                    val displayStatus = when {
                        missedCount >= 3 -> "Suspenso"
                        missedCount > 0 -> "$missedCount Faltas"
                        else -> "Regular"
                    }

                    BeneficiaryCard(
                        name = item.name,
                        processNumber = item.id.toString(),
                        status = displayStatus,
                        onClick = {
                            if (missedCount >= 3) {
                                onOpenDialog(item)
                            } else {
                                onClick(item)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BeneficiaryCard(
    name: String,
    processNumber: String,
    status: String,
    onClick: () -> Unit
) {
    val isSuspended = status == "Suspenso"
    val isWarning = status.contains("Faltas")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isSuspended) Color.Red else GreenIPCA),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Processo nº $processNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Surface(
                color = when {
                    isSuspended -> Color(0xFFFFDAD4)
                    isWarning -> Color(0xFFFFF3E0)
                    else -> Color(0xFFE8F5E9)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = when {
                        isSuspended -> Color.Red
                        isWarning -> Color(0xFFE65100)
                        else -> Color(0xFF136342)
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SuspensionDecisionDialog(
    beneficiaryName: String,
    onConfirmSuspension: () -> Unit,
    onForgive: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Limite de Faltas Atingido", fontWeight = FontWeight.Bold) },
        text = { Text("O beneficiário $beneficiaryName atingiu 3 faltas. Deseja aplicar a suspensão ou perdoar?") },
        confirmButton = {
            Button(
                onClick = onConfirmSuspension,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
            ) {
                Text("Confirmar Suspensão", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onForgive) {
                Text("Perdoar", color = GreenIPCA)
            }
        }
    )
}