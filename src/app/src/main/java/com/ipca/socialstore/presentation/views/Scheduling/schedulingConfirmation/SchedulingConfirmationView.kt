package com.ipca.socialstore.presentation.views.Scheduling.schedulingConfirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA

@Composable
fun SchedulingConfirmationView(
    modifier: Modifier,
    navController: NavController,
    userRole: UserRole
) {
    val viewModel: SchedulingConfirmationViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    SchedulingConfirmationContent(
        modifier = modifier,
        uiState = uiState,
        updateObservation = { value -> viewModel.updateNoteUi(value)},
        onConfirm = {viewModel.updateNote() },
        onAccept = {viewModel.acceptScheduling()},
        onDecline = {viewModel.declineScheduling()},
        userRole = userRole
    )
}

@Composable
fun SchedulingConfirmationContent(
    modifier: Modifier,
    uiState: SchedulingConfirmationState,
    updateObservation: (String) -> Unit,
    onConfirm: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    userRole: UserRole
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        Text(
            text = "Confirmar Agendamento",
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
                            .background(GreenIPCA, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Data agendada: ${uiState.scheduling?.schedulingDate ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Observações para a entrega",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.note ?: "",
            onValueChange = { value -> updateObservation(value) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = { Text("Adicione notas ou observações sobre a entrega...") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        if (userRole == UserRole.BENEFICIARY){
            if (uiState.scheduling?.state == "accept"){
                Button(
                    onClick = { onConfirm() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {

                    Text("Enviar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            if (uiState.scheduling?.state == "in_Progress") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAccept() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF136342))
                    ) {
                        Text("Aceitar", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = { onDecline() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Recusar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

            }
        }
        else{
            if (uiState.scheduling?.state == "accept"){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onDecline() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }



    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SchedulingConfirmationPreview() {
    val mockBeneficiary = BeneficiaryModel(
        id = 4,
        name = "Diogo",
        birthDate = "1995-08-20",
        academicId = 2,
        phoneNumber = ""
    )

    // 2. Simulando o Agendamento Ativo ('accept')
    val mockScheduling = SchedulingModel(
        id = 6,
        schedulingDate = "2026-01-24",
        beneficiaryId = 4,
        state = "canceled",
        reason = null,
        note = null
    )

    // 3. Estado inicial com uma observação de exemplo
    val mockUiState = SchedulingConfirmationState(
        beneficiary = mockBeneficiary,
        scheduling = mockScheduling,
        error = null,
        isLoading = false
    )

    MaterialTheme {
        SchedulingConfirmationContent(
            modifier = Modifier,
            uiState = mockUiState,
            updateObservation = {},
            onConfirm = {},
            onDecline = {},
            onAccept = {},
            userRole = UserRole.ADMIN
        )
    }
}