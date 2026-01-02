import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.enums.RequestType
import com.ipca.socialstore.data.enums.TypeCourse
import com.ipca.socialstore.presentation.ui.components.SocialStoreDropdown
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldValueComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ApplicationForm(
    modifier: Modifier = Modifier,

    // --- 1. Individual Values (Personal Data) ---
    name: String,
    birthDate: String,
    cc: String,
    phoneNumber: String,
    schoolYear: String,
    requestType: String, // Pass the label or identifier

    // --- 2. Logic Values ---
    isStudent: Boolean = false,
    isAdmin: Boolean = false,

    // --- 3. Individual Values (Academic Data) ---
    academicCourseType: String,
    academicCourseName: String,
    academicStudentNumber: String,

    // --- 4. Callbacks (Events) ---
    onNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onCcChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onRequestTypeChange: (String) -> Unit,

    onAcademicTypeChange: (String) -> Unit,
    onAcademicCourseChange: (String) -> Unit,
    onAcademicNumberChange: (String) -> Unit,
) {
    var isRequestTypeExpanded by remember { mutableStateOf(false) }
    var isCourseTypeExpanded by remember { mutableStateOf(false) }
    var isStudentLocal by remember { mutableStateOf(isStudent) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                        onBirthDateChange(format.format(date))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldStringComponent(
            modifier = Modifier,
            label = "Name",
            value = name,
            icon = Icons.Default.Star,
            onValueUpdate = onNameChange
        )

        TextFieldDateComponent(
            modifier = Modifier,
            label = "Data de Nascimento",
            date = birthDate,
            onDateUpdate = onBirthDateChange,
            onDatePickerUpdate = {showDatePicker = !showDatePicker}
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "CC",
            value = cc,
            icon = Icons.Default.Star,
            onValueUpdate = onCcChange
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "Telemovel",
            value = phoneNumber,
            icon = Icons.Default.Star,
            onValueUpdate = onPhoneChange
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "Ano Letivo",
            value = schoolYear,
            icon = Icons.Default.Star,
            onValueUpdate = onYearChange
        )

        SocialStoreDropdown<RequestType>(
            expanded = isRequestTypeExpanded,
            onExpandedChange = { isRequestTypeExpanded = it },
            selectedOption = RequestType.entries.find { it.label == requestType },
            onOptionSelected = { newEnum -> onRequestTypeChange(newEnum.label) },
            options = RequestType.entries,
            label = "Tipo de Pedido",
            getLabel = { it.label }
        )

        if(!isAdmin){
            HorizontalDivider(thickness = 2.dp, color = Color.LightGray)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "É estudante atualmente?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Habilita o preenchimento dos dados escolares",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = isStudent,
                    onCheckedChange = {isStudentLocal = !isStudentLocal}
                )
            }
        }

        if (isStudent) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Academic Information", style = MaterialTheme.typography.labelLarge)

                SocialStoreDropdown<TypeCourse>(
                    expanded = isCourseTypeExpanded,
                    onExpandedChange = { isCourseTypeExpanded = it },
                    selectedOption = TypeCourse.entries.find { it.label == academicCourseType },
                    onOptionSelected = { newEnum -> onAcademicTypeChange(newEnum.label) },
                    options = TypeCourse.entries,
                    label = "Curso",
                    getLabel = { it.label }
                )

                TextFieldStringComponent(
                    modifier = Modifier,
                    label = "Nome do Curso",
                    value = academicCourseName,
                    icon = Icons.Default.Star,
                    onValueUpdate = onAcademicCourseChange
                )

                TextFieldValueComponent(
                    modifier = Modifier,
                    label = "Numero de Estudante",
                    value = academicStudentNumber,
                    icon = Icons.Default.Star,
                    onValueUpdate = onAcademicNumberChange
                )
            }
        }
    }
}