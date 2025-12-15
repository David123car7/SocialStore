import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- ECRÃ DE FORMULÁRIO COMPLETO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationFormScreenMockup() {

    // --- ESTADOS (Simulando o ViewModel) ---
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var ccNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    // Dropdowns
    var requestType by remember { mutableStateOf("") }
    var isRequestTypeExpanded by remember { mutableStateOf(false) }

    // Student Status
    var isStudent by remember { mutableStateOf(false) } // O Switch controla isto

    // Academic Data
    var studentNumber by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var typeCourse by remember { mutableStateOf("") }
    var isTypeCourseExpanded by remember { mutableStateOf(false) }
    var schoolYear by remember { mutableStateOf("") }
    var isSchoolYearExpanded by remember { mutableStateOf(false) }

    // Utilitários para Datas
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Listas para Dropdowns
    val requestTypesList = listOf("Apoio Alimentar", "Bolsa de Estudo", "Alojamento", "Material Informático")
    val courseTypesList = listOf("CTeSP", "Licenciatura", "Mestrado", "Pós-Graduação")
    val schoolYearsList = listOf("1º Ano", "2º Ano", "3º Ano", "4º Ano", "5º Ano")

    // --- LÓGICA DO DATE PICKER ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        birthDate = format.format(date)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Preencher Candidatura") },
                navigationIcon = {
                    IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ==========================================
            // GRUPO 1: INFORMAÇÃO PESSOAL
            // ==========================================
            SectionHeader("Dados Pessoais")

            // Nome
            OutlinedTextField(
                value = name,
                onValueChange = { name = it }, // onNameUpdate
                label = { Text("Nome Completo") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Data de Nascimento
            OutlinedTextField(
                value = birthDate,
                onValueChange = {}, // onBirthDateUpdate
                label = { Text("Data de Nascimento") },
                placeholder = { Text("DD/MM/AAAA") },
                leadingIcon = { Icon(Icons.Default.Star, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false, // Read-only visualmente
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            // Hack para clique funcionar sobre o campo desabilitado
            Box(Modifier.clickable { showDatePicker = true })

            // CC e Telemóvel (Lado a Lado)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = ccNumber,
                    onValueChange = { if (it.length <= 8) ccNumber = it }, // onCcUpdate
                    label = { Text("Nº CC") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it }, // onPhoneUpdate
                    label = { Text("Telemóvel") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }

            // Tipo de Pedido (Dropdown)
            ExposedDropdownMenuBox(
                expanded = isRequestTypeExpanded,
                onExpandedChange = { isRequestTypeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = requestType,
                    onValueChange = {}, // onRequestTypeUpdate
                    readOnly = true,
                    label = { Text("Tipo de Pedido") },
                    leadingIcon = { Icon(Icons.Default.Star, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRequestTypeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isRequestTypeExpanded,
                    onDismissRequest = { isRequestTypeExpanded = false }
                ) {
                    requestTypesList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                requestType = item
                                isRequestTypeExpanded = false
                            }
                        )
                    }
                }
            }

            // ==========================================
            // GRUPO 2: ESTATUTO DE ESTUDANTE
            // ==========================================
            Divider()

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("É estudante atualmente?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Habilita o preenchimento dos dados escolares", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Switch(
                    checked = isStudent,
                    onCheckedChange = { isStudent = it } // onIsStudentUpdate
                )
            }

            // ==========================================
            // GRUPO 3: DADOS ACADÉMICOS (Condicional)
            // ==========================================
            AnimatedVisibility(
                visible = isStudent,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    SectionHeader("Dados Académicos")

                    // Nº de Aluno
                    OutlinedTextField(
                        value = studentNumber,
                        onValueChange = { studentNumber = it }, // onStudentNumberUpdate
                        label = { Text("Número de Aluno") },
                        leadingIcon = { Icon(Icons.Default.Star, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Tipo de Curso (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = isTypeCourseExpanded,
                        onExpandedChange = { isTypeCourseExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = typeCourse,
                            onValueChange = {}, // onTypeCourseUpdate
                            readOnly = true,
                            label = { Text("Grau/Tipo de Curso") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeCourseExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isTypeCourseExpanded,
                            onDismissRequest = { isTypeCourseExpanded = false }
                        ) {
                            courseTypesList.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        typeCourse = item
                                        isTypeCourseExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Nome do Curso
                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { courseName = it }, // onCourseUpdate
                        label = { Text("Nome do Curso") },
                        leadingIcon = { Icon(Icons.Default.Star, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Ano Escolar (Dropdown)
                    ExposedDropdownMenuBox(
                        expanded = isSchoolYearExpanded,
                        onExpandedChange = { isSchoolYearExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = schoolYear,
                            onValueChange = {}, // onYearUpdate
                            readOnly = true,
                            label = { Text("Ano Curricular") },
                            leadingIcon = { Icon(Icons.Default.Star, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSchoolYearExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isSchoolYearExpanded,
                            onDismissRequest = { isSchoolYearExpanded = false }
                        ) {
                            schoolYearsList.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        schoolYear = item
                                        isSchoolYearExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botão Final
            Button(
                onClick = { /* Submeter */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continuar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- Componente Auxiliar para Títulos ---
@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ApplicationFormPreview() {
    MaterialTheme {
        ApplicationFormScreenMockup()
    }
}