package com.ipca.socialstore.presentation.views.application

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.utils.getFileNameFromUri
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun ApplicationView(modifier: Modifier){
    val applicationViewModel: ApplicationViewModel = hiltViewModel()
    val uiState by applicationViewModel.uiState

    ApplicationViewContent(
        modifier = modifier,
        uiState = uiState,

        // --- Personal Info Updates ---
        onNameUpdate = applicationViewModel::updateName,
        onYearUpdate = applicationViewModel::updateSchoolYear,
        onBirthDateUpdate = applicationViewModel::updateBirthDate,
        onCcUpdate = applicationViewModel::updateCc,
        onPhoneUpdate = applicationViewModel::updatePhoneNumber,
        onEmailUpdate = applicationViewModel::updateEmail,
        onRequestTypeUpdate = applicationViewModel::updateRequestType,

        // --- Student Status Updates ---
        onIsStudentUpdate = applicationViewModel::updateIsStudent,

        // --- Academic Data Updates ---
        onTypeCourseUpdate = applicationViewModel::updateTypeCourse,
        onCourseUpdate = applicationViewModel::updateCourse,
        onStudentNumberUpdate = applicationViewModel::updateStudentNumber,

        // --- Files & Submit ---
        onAddFiles = applicationViewModel::addFiles,
        onRemoveFile = applicationViewModel::removeFile,
        onSubmitApplication = applicationViewModel::createApplication
    )
}

@Composable
fun ApplicationViewContent(
    modifier: Modifier = Modifier,
    uiState: ApplicationState,

    // --- Actions/Callbacks ---
    onIsStudentUpdate: (Boolean) -> Unit,
    onNameUpdate: (String) -> Unit,
    onYearUpdate: (String) -> Unit,
    onBirthDateUpdate: (String) -> Unit,
    onCcUpdate: (String) -> Unit,
    onPhoneUpdate: (String) -> Unit,
    onEmailUpdate: (String) -> Unit,
    onRequestTypeUpdate: (String) -> Unit,

    // Academic Data
    onTypeCourseUpdate: (String) -> Unit,
    onCourseUpdate: (String) -> Unit,
    onStudentNumberUpdate: (String) -> Unit,

    // Files & Submit
    onAddFiles: (List<Uri>) -> Unit,
    onRemoveFile: (Uri) -> Unit,
    onSubmitApplication: () -> Unit
) {

    val multiFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        onAddFiles(uris)
    }

    // Enable scrolling for long forms
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // <--- Critical for long forms
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Beneficiary Application",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = uiState.application.name,
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onNameUpdate
        )

        TextField(
            value = uiState.application.birthDate,
            label = { Text("Birth Date (YYYY-MM-DD)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onBirthDateUpdate
        )

        TextField(
            value = uiState.application.cc,
            label = { Text("Citizen Card (CC)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onCcUpdate
        )

        TextField(
            value = uiState.application.phoneNumber,
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onPhoneUpdate
        )

        TextField(
            value = uiState.application.email,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onEmailUpdate
        )

        TextField(
            value = if ((uiState.application.schoolYear) == 0) "" else uiState.application.schoolYear.toString(),
            label = { Text("School Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onYearUpdate
        )

        TextField(
            value = uiState.application.requestType,
            label = { Text("Request Type (e.g. Food, Housing)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onRequestTypeUpdate
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = uiState.isStudent,
                onCheckedChange = onIsStudentUpdate
            )
            Text("I am currently a Student")
        }

        if (uiState.isStudent) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Academic Information", style = MaterialTheme.typography.labelLarge)

                TextField(
                    value = uiState.academicData.typeCourse,
                    label = { Text("Type of Course (e.g. CTeSP, Degree)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onValueChange = onTypeCourseUpdate
                )

                TextField(
                    value = uiState.academicData.course,
                    label = { Text("Course Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onValueChange = onCourseUpdate
                )

                TextField(
                    value = uiState.academicData.studenNumber,
                    label = { Text("Student Number") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onValueChange = onStudentNumberUpdate
                )
            }
        }
        /*
        OutlinedButton(
            onClick = { multiFilePicker.launch("application/pdf") }, // Filter for PDFs
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Attach Documents (PDF)")
        }

        if (uiState.selectedFiles.isNotEmpty()) {
            Text(
                text = "Selected Documents:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 8.dp)
            ) {
                items(uiState.selectedFiles) { uri ->
                    FileRowItem(uri = uri, onRemoveFile = { onRemoveFile(uri) })
                }
            }
        }*/

        Button(
            onClick = {
                onSubmitApplication()
            }
        ){
            Text("Submit")
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
fun ApplicationPreview() {
    SocialStoreTheme {
        val uiState = ApplicationState(
            isLoading = false,
            error = null,
            isSuccess = false,
            selectedFiles = emptyList(),
            application = ApplicationModel(stateId = null,schoolYear = 0, name = "", birthDate = "", cc = "", phoneNumber = "", email = "", requestType = ""),
            academicData = AcademicModel(typeCourse = "", course = "", studenNumber = "")
        )

        ApplicationViewContent(
            modifier = Modifier,
            uiState = uiState,

            // --- Personal ---
            onNameUpdate = {},
            onYearUpdate = {},
            onBirthDateUpdate = {},
            onCcUpdate = {},
            onPhoneUpdate = {},
            onEmailUpdate = {},
            onRequestTypeUpdate = {},

            // --- Student ---
            onIsStudentUpdate = {},

            // --- Academic ---
            onTypeCourseUpdate = {},
            onCourseUpdate = {},
            onStudentNumberUpdate = {},

            // --- Files ---
            onAddFiles = {},
            onRemoveFile = {},
            onSubmitApplication = {}
        )
    }
}