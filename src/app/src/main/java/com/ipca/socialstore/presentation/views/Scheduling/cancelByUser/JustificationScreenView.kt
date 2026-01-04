package com.ipca.socialstore.presentation.views.Scheduling.cancelByUser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel

@Composable
fun JustificationScreenView(
    modifier: Modifier,
    navController: NavController
){

    val viewModel : JustificationScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    JustificationScreenContent(
        modifier = modifier,
        uiState = uiState,
        updateReason = {value -> viewModel.updateReasonUi(value)},
        onUpdate = {viewModel.updateReason()}
    )
}

@Composable
fun JustificationScreenContent(
    modifier: Modifier,
    uiState: JustificationState,
    updateReason : (value : String) -> Unit,
    onUpdate : () -> Unit
){
    val isJustified = uiState.scheduling?.state == "justified"
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {

        Text(
            text = "Justificar Falta",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = uiState.beneficiary?.name ?: "Beneficiário",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Processo nº ${uiState.beneficiary?.id ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFEF5350), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Data da falta: ${uiState.scheduling?.schedulingDate ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Motivo da ausência",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.reason ?: "",
            onValueChange = { value -> if (!isJustified) updateReason(value)},
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = { Text("Escreva aqui o motivo detalhado...") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )


        Spacer(modifier = Modifier.weight(1f))

        if (!isJustified){
            Button(
                onClick = { onUpdate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
            ) {
                Text("Enviar Justificação", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

    }
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun JustificationScreenPreview() {
    // Simulando os modelos conforme a tua estrutura
    val mockBeneficiary = BeneficiaryModel(
        id = 3,
        name = "David",
        birthDate = "1990-05-15",
        addressId = 1
    )

    val mockScheduling = SchedulingModel(
        id = 2,
        schedulingDate = "22/01/2026",
        beneficiaryId = 3,
        state = "canceled",
        reason = "TEste"
    )

    val mockUiState = JustificationState(
        beneficiary = mockBeneficiary,
        scheduling = mockScheduling,
        isLoading = false
    )

    MaterialTheme {
        JustificationScreenContent(
            modifier = Modifier,
            uiState = mockUiState,
            updateReason = {},
            onUpdate = {}
        )
    }
}

 */

