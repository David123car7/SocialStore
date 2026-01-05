package com.ipca.socialstore.presentation.views.Scheduling.mainPage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.models.SchedulingReceiverModel
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SchedulingMainPageView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : SchedulingMainPageViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    var currentCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(currentCalendar) {
        val month = currentCalendar.get(Calendar.MONTH) + 1
        val year = currentCalendar.get(Calendar.YEAR)
        viewModel.getAllBeneficiariesByMonth(month, year)
    }

    LaunchedEffect(uiState.beneficiary == null) {
        viewModel.getAllBeneficiary()
    }
    SchedulingMainContent(
        modifier = modifier,
        uiState = uiState,
        currentCalendar = currentCalendar,
        onMonthChange = { currentCalendar = it },
        onCancelScheduling = { value -> viewModel.cancelScheduling(value) },
        onCreateScheduling = {viewModel.createScheduling()},
        onUpdateDate = {value -> viewModel.updateSchedulingDate(value)},
        onUpdateBeneficiaryId = {value -> viewModel.updateBeneficiaryId(value)}
    )
}

@Composable
fun SchedulingMainContent(
    modifier : Modifier,
    uiState: SchedulingMainState,
    currentCalendar: Calendar,
    onMonthChange: (Calendar) -> Unit,
    onCancelScheduling: (value: Int) -> Unit,
    onCreateScheduling: () -> Unit,
    onUpdateDate : (String) -> Unit,
    onUpdateBeneficiaryId: (Int) -> Unit
){
    var showPopup by remember { mutableStateOf(false) }
    var selectDate by remember { mutableStateOf("") }
    var selectedBeneficiary by remember {
        mutableStateOf(uiState.beneficiary)
    }

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        CustomSchedulingCalendar(
            uiState = uiState,
            currentCalendar = currentCalendar,
            onMonthChange = onMonthChange,
            onClickDay = { date ->
                selectDate = date
                onUpdateDate(date)
                showPopup = true
            }
        )

        Text(
            text = "Próximas Marcações",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(uiState.scheduling ?: emptyList()){ index, item ->
                SchedulingItemCard(
                    beneficiaryName = item.name,
                    date = item.date,
                    onCancelClick = { onCancelScheduling(item.schedulingId) }
                )
            }
        }

        if (showPopup) {
            var date by remember { mutableStateOf("") }
            var note by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showPopup = false },
                title = { Text("Novo Agendamento: $selectDate", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (uiState.beneficiary != null){
                            OutlinedTextField(
                                value = uiState.beneficiary.name,
                                onValueChange = {},
                                label = { Text("Nome") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        else{
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedBeneficiary?.name ?: "Selecionar Beneficiário",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Beneficiário") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = !expanded }) {
                                            Icon(Icons.Default.ArrowDropDown, null)
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth(0.7f)
                                ) {
                                    uiState.listBeneficiary?.forEach { beneficiary ->
                                        DropdownMenuItem(
                                            text = { Text(beneficiary.name) },
                                            onClick = {
                                                selectedBeneficiary = beneficiary
                                                onUpdateBeneficiaryId(beneficiary.id!!)
                                                expanded = false

                                            }
                                        )
                                    }
                                }
                            }
                        }
                        OutlinedTextField(
                            value = selectDate,
                            onValueChange = {},
                            label = { Text("Data") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = {  },
                            label = { Text("Notas Adicionais") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCreateScheduling()
                            showPopup = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF136342))
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    TextButton(onClick = { showPopup = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun CustomSchedulingCalendar(
    uiState: SchedulingMainState,
    currentCalendar: Calendar,
    onMonthChange: (Calendar) -> Unit,
    onClickDay : (String) -> Unit
) {
    val daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthTitle = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentCalendar.time)

    val bookedDays = remember(uiState.scheduling) {
        uiState.scheduling?.map { it.date.split("-").last().toInt() }?.toSet() ?: emptySet()
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = monthTitle, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    IconButton(onClick = {
                        val new = currentCalendar.clone() as Calendar
                        new.add(Calendar.MONTH, -1)
                        onMonthChange(new)
                    }) { Text("<", fontWeight = FontWeight.Bold) }
                    IconButton(onClick = {
                        val new = currentCalendar.clone() as Calendar
                        new.add(Calendar.MONTH, 1)
                        onMonthChange(new)
                    }) { Text(">", fontWeight = FontWeight.Bold) }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { day ->
                    Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.Gray)
                }
            }

            val firstDayOfMonth = (currentCalendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK) - 1
            var dayCounter = 1

            for (i in 0..5) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (j in 0..6) {
                        val isDayInMonth = (i == 0 && j >= firstDayOfMonth) || (i > 0 && dayCounter <= daysInMonth)
                        val currentDay = dayCounter

                        Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                            if (isDayInMonth && dayCounter <= daysInMonth) {
                                val isBooked = bookedDays.contains(dayCounter)

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(if (isBooked) Color(0xFF136342) else Color.Transparent, CircleShape)
                                        .clickable(enabled = isDayInMonth){
                                            val dateStr = String.format(
                                                Locale.US,
                                                "%04d-%02d-%02d",
                                                currentCalendar.get(Calendar.YEAR),
                                                currentCalendar.get(Calendar.MONTH) + 1,
                                                currentDay
                                            )
                                            onClickDay(dateStr)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayCounter.toString(),
                                        color = if (isBooked) Color.White else Color.Black,
                                        fontWeight = if (isBooked) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                dayCounter++
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SchedulingItemCard(beneficiaryName: String?, date: String, onCancelClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                if (beneficiaryName != null) {
                    Text(text = beneficiaryName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                }
                Text(text = "Agendamento: $date", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
            }
            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF136342)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SchedulingMainPreview() {
    SocialStoreTheme {
        val mockUiState = SchedulingMainState(
            scheduling = listOf(
                SchedulingReceiverModel(
                    name = "kazzio",
                    schedulingId = 1,
                    date = "2026-01-02"
                ),
                SchedulingReceiverModel(name = "kazzio",   schedulingId = 2, date = "2026-01-15"),
                SchedulingReceiverModel(name = "kazzio",   schedulingId = 13, date = "2026-01-22")
            ),
            isLoading = false,
            error = null
        )

        val previewCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        SchedulingMainContent(
            modifier = Modifier.padding(top = 64.dp), // Simula a TopAppBar "Social Store"
            uiState = mockUiState,
            currentCalendar = previewCalendar,
            onMonthChange = {},
            onCancelScheduling = {},
            onCreateScheduling = {},
            onUpdateBeneficiaryId = {},
            onUpdateDate = {}
        )
    }
}