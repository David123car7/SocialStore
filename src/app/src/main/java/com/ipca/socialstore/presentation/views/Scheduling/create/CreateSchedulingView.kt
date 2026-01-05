package com.ipca.socialstore.presentation.views.Scheduling.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme


@Composable
fun CreateSchedulingView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : CreateSchedulingViewModel = hiltViewModel()
    val uiState by viewModel.uiState


    CreateSchedulingViewContent(
        modifier = modifier,
        uiState = uiState,

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSchedulingViewContent(
    modifier: Modifier = Modifier,
    uiState: CreateSchedulingState,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Selecionar Beneficiário") }

    // Estado para o Calendário
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Seletor de Beneficiário (Teu código atual)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedText)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                uiState.beneficiaries?.forEach { beneficiary ->
                    DropdownMenuItem(
                        text = { Text("ID: ${beneficiary.id} - ${beneficiary.name}") },
                        onClick = {
                            selectedText = beneficiary.name
                            expanded = false
                        }
                    )
                }
            }
        }

        // 2. Campo de Data que abre o Calendário
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            val dateLabel = datePickerState.selectedDateMillis?.let {
                val date = java.util.Date(it)
                val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                format.format(date)
            } ?: "Selecionar Data da Entrega"

            Text(dateLabel)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.DateRange, contentDescription = null)
        }


        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Confirmar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /* Lógica de salvar */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Text("Criar Agendamento", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateSchedulingPreview() {
    SocialStoreTheme {
        // 1. Criar dados fictícios para o Preview baseados nos teus modelos
        val mockBeneficiaries = listOf(
            com.ipca.socialstore.data.models.BeneficiaryModel(
                id = 4,
                name = "Diogo",
                birthDate = "1995-08-20",
                academicId = 2
            ),
            com.ipca.socialstore.data.models.BeneficiaryModel(
                id = 3,
                name = "David",
                birthDate = "1990-05-15",
                academicId = 1
            )
        )

        // 2. Simular o estado da UI com a lista carregada
        val mockUiState = CreateSchedulingState(
            beneficiaries = mockBeneficiaries,
            isLoading = false
        )

        // 3. Renderizar o conteúdo
        CreateSchedulingViewContent(
            modifier = Modifier.padding(16.dp),
            uiState = mockUiState
        )
    }
}

