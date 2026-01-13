import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.School
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.enums.TypeCourse
import com.ipca.socialstore.presentation.ui.components.BooleanSwitchComponent
import com.ipca.socialstore.presentation.ui.components.RequestTypeSelection
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
    requestType: String,

    // --- 2. Logic Values ---
    isStudent: Boolean = false,
    isEditing: Boolean = false,

    // --- 3. Individual Values (Academic Data) ---
    academicCourseType: String,
    academicCourseName: String,
    academicStudentNumber: String,
    scholarShipValue: String,

    // --- 4. Callbacks (Events) ---
    onNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onCcChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onRequestTypeChange: (String) -> Unit,
    onOffCountryUpdate:(Boolean) -> Unit,
    onFaesUpdate:(Boolean) -> Unit,
    onScholarShipValueUpdate:(String) -> Unit,
    onScholarShipUpdate:(Boolean)-> Unit,


    onAcademicTypeChange: (String) -> Unit,
    onAcademicCourseChange: (String) -> Unit,
    onAcademicNumberChange: (String) -> Unit,
    onIsStudentUpdate:(Boolean) -> Unit,
) {
    var isOffCountryChecked by remember { mutableStateOf(false) }
    var isFaesChecked by remember { mutableStateOf(false) }
    var isScholarShipChecked by remember { mutableStateOf(false) }


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
            icon = Icons.Outlined.Person,
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
            icon = Icons.Outlined.CreditCard,
            onValueUpdate = onCcChange
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "Telemovel",
            value = phoneNumber,
            icon = Icons.Outlined.Phone,
            onValueUpdate = onPhoneChange
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "Ano Letivo",
            value = schoolYear,
            icon = Icons.Outlined.DateRange,
            onValueUpdate = onYearChange
        )

        if(!isEditing){
            RequestTypeSelection(
                currentSelectionString = requestType,
                onSelectionChange = { newCombinedString ->
                    onRequestTypeChange(newCombinedString)
                }
            )

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
                    checked = isStudentLocal,
                    onCheckedChange = {
                        isStudentLocal = !isStudentLocal
                        onIsStudentUpdate(isStudentLocal)
                    }
                )
            }
        }

        if (isStudentLocal) {
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
                    getLabel = { it.label },
                    icon = Icons.Outlined.School
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

        if(!isEditing) {
            BooleanSwitchComponent(
                title = "Pais de Terceiros",
                checked = isOffCountryChecked,
                onCheckedChange = {
                    isOffCountryChecked = !isOffCountryChecked
                    onOffCountryUpdate(isOffCountryChecked)
                }
            )

            BooleanSwitchComponent(
                title = "Fundo de Apoio de Emergência Social (FAES)?",
                checked = isFaesChecked,
                onCheckedChange = {
                    isFaesChecked = !isFaesChecked
                    onFaesUpdate(isFaesChecked)
                }
            )

            BooleanSwitchComponent(
                title = "É beneficiário de alguma bolsa de estudo ou apoio?",
                checked = isScholarShipChecked,
                onCheckedChange = {
                    isScholarShipChecked = !isScholarShipChecked
                    onScholarShipUpdate(isScholarShipChecked)
                }
            )

            if(isScholarShipChecked){
                TextFieldValueComponent(
                    modifier = Modifier,
                    label = "Valor",
                    value = scholarShipValue,
                    icon = Icons.Default.Star,
                    onValueUpdate = onScholarShipValueUpdate
                )
            }
        }
    }
}