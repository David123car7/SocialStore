package com.ipca.socialstore.presentation.views.application.createApplication

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipca.socialstore.data.enums.RequestType
import com.ipca.socialstore.data.enums.TypeCourse
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.ui.components.SocialStoreDropdown
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldValueComponent
import com.ipca.socialstore.presentation.utils.getFileNameFromUri
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateApplicationView(modifier: Modifier){
    val applicationViewModel: CreateApplicationViewModel = hiltViewModel()
    val uiState by applicationViewModel.uiState

    CreateApplicationViewContent(
        modifier = modifier,
        uiState = uiState,

        // --- Personal Info Updates ---
        onNameUpdate = applicationViewModel::updateName,
        onYearUpdate = applicationViewModel::updateSchoolYear,
        onBirthDateUpdate = applicationViewModel::updateBirthDate,
        onCcUpdate = applicationViewModel::updateCc,
        onPhoneUpdate = applicationViewModel::updatePhoneNumber,
        onRequestTypeUpdate = applicationViewModel::updateRequestType,

        // --- Student Status Updates ---
        onIsStudentUpdate = applicationViewModel::updateIsStudent,

        // --- Academic Data Updates ---
        onTypeCourseUpdate = applicationViewModel::updateTypeCourse,
        onCourseUpdate = applicationViewModel::updateCourse,
        onStudentNumberUpdate = applicationViewModel::updateStudentNumber,
        onSubmitApplication = applicationViewModel::createApplication
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateApplicationViewContent(
    modifier: Modifier = Modifier,
    uiState: CreateApplicationState,

    // --- Actions/Callbacks ---
    onIsStudentUpdate: (Boolean) -> Unit,
    onNameUpdate: (String) -> Unit,
    onYearUpdate: (String) -> Unit,
    onBirthDateUpdate: (String) -> Unit,
    onCcUpdate: (String) -> Unit,
    onPhoneUpdate: (String) -> Unit,
    onRequestTypeUpdate: (String) -> Unit,

    // Academic Data
    onTypeCourseUpdate: (String) -> Unit,
    onCourseUpdate: (String) -> Unit,
    onStudentNumberUpdate: (String) -> Unit,

    onSubmitApplication: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    var requestTypeExpanded by remember { mutableStateOf(false) }
    var courseTypeExpanded by remember { mutableStateOf(false) }


    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                        onBirthDateUpdate(format.format(date))
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
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldStringComponent(
            modifier = Modifier,
            label = "Email",
            value = uiState.application.name,
            icon = Icons.Default.Star,
            onValueUpdate = onNameUpdate
        )

        TextFieldDateComponent(
            modifier = Modifier,
            label = "Data de Nascimento",
            uiState.application.birthDate,
            onDateUpdate = onBirthDateUpdate,
            onDatePickerUpdate = {showDatePicker = true}
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "CC",
            value = uiState.application.cc,
            icon = Icons.Default.Star,
            onValueUpdate = onCcUpdate
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "Telemovel",
            value = uiState.application.phoneNumber,
            icon = Icons.Default.Star,
            onValueUpdate = onPhoneUpdate
        )

        TextFieldValueComponent(
            modifier = Modifier,
            label = "Ano Letivo",
            value = if ((uiState.application.schoolYear) == 0) "" else uiState.application.schoolYear.toString(),
            icon = Icons.Default.Star,
            onValueUpdate = onYearUpdate
        )

        SocialStoreDropdown<RequestType>(
            expanded = requestTypeExpanded,
            onExpandedChange = { requestTypeExpanded = it },
            selectedOption = RequestType.entries.find{it.label == uiState.application.requestType},
            onOptionSelected = { newEnum ->
                onRequestTypeUpdate(newEnum.label)
            },
            options = RequestType.entries,
            label = "Tipo de Pedido",
            getLabel = { it.label }
        )

        HorizontalDivider(thickness = 2.dp)

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
                checked = uiState.isStudent,
                onCheckedChange = onIsStudentUpdate
            )
        }

        if (uiState.isStudent) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Academic Information", style = MaterialTheme.typography.labelLarge)

                SocialStoreDropdown<TypeCourse>(
                    expanded = courseTypeExpanded,
                    onExpandedChange = { courseTypeExpanded = it },
                    selectedOption = TypeCourse.entries.find{it.label == uiState.academicData?.typeCourse!!},
                    onOptionSelected = { newEnum ->
                        onTypeCourseUpdate(newEnum.label)
                    },
                    options = TypeCourse.entries,
                    label = "Curso",
                    getLabel = { it.label }
                )

                TextFieldStringComponent(
                    modifier = Modifier,
                    label = "Nome do Curso",
                    value = uiState.academicData?.course!!,
                    icon = Icons.Default.Star,
                    onValueUpdate = onCourseUpdate
                )

                TextFieldValueComponent(
                    modifier = Modifier,
                    label = "Numero de Estudante",
                    value = uiState.academicData.studenNumber,
                    icon = Icons.Default.Star,
                    onValueUpdate = onStudentNumberUpdate
                )
            }
        }

        Button(
            onClick = onSubmitApplication,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }

        if (uiState.error != null) {
            Text(text = uiState.error.asString(), modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun FileRowItem(uri: Uri, onRemoveFile:(uri: Uri?)->Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val fileName = getFileNameFromUri(LocalContext.current, uri = uri)

            Text(
                text = fileName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )

            IconButton(
                onClick = { onRemoveFile(uri) },
                modifier = Modifier.size(24.dp)
            ) {
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateApplicationViewPreview() {
    SocialStoreTheme {
        val uiState = CreateApplicationState(
            isLoading = false,
            error = null,
            isSuccess = false,
            selectedFiles = emptyList(),
            application = ApplicationModel(
                stateId = -1,
                schoolYear = 0,
                name = "",
                birthDate = "",
                cc = "",
                phoneNumber = "",
                email = "",
                requestType = "",
                academicId = null
            ),
            academicData = AcademicModel(typeCourse = "", course = "", studenNumber = "")
        )

        CreateApplicationViewContent(
            modifier = Modifier,
            uiState = uiState,

            // --- Personal ---
            onNameUpdate = {},
            onYearUpdate = {},
            onBirthDateUpdate = {},
            onCcUpdate = {},
            onPhoneUpdate = {},
            onRequestTypeUpdate = {},

            // --- Student ---
            onIsStudentUpdate = {},

            // --- Academic ---
            onTypeCourseUpdate = {},
            onCourseUpdate = {},
            onStudentNumberUpdate = {},

            onSubmitApplication = {}
        )
    }
}