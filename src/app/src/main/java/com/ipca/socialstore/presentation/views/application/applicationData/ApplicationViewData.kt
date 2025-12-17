package com.ipca.socialstore.presentation.views.application.applicationData

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun ApplicationViewDataContent(
    modifier: Modifier = Modifier,
    uiState: ApplicationState2,
    onIsDataUpdate: () -> Unit,
    onNameUpdate: (String) -> Unit,
    onYearUpdate: (String) -> Unit,
    onBirthDateUpdate: (String) -> Unit,
    onCcUpdate: (String) -> Unit,
    onPhoneUpdate: (String) -> Unit,
    onRequestTypeUpdate: (String) -> Unit,
    onTypeCourseUpdate: (String) -> Unit,
    onCourseUpdate: (String) -> Unit,
    onStudentNumberUpdate: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Beneficiary Application",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = uiState.application?.name ?: "",
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onNameUpdate
        )

        TextField(
            value = uiState.application?.birthDate ?: "",
            label = { Text("Birth Date (YYYY-MM-DD)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onBirthDateUpdate
        )

        TextField(
            value = uiState.application?.cc ?: "",
            label = { Text("Citizen Card (CC)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onCcUpdate
        )

        TextField(
            value = uiState.application?.phoneNumber ?: "",
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onPhoneUpdate
        )

        TextField(
            value = if ((uiState.application?.schoolYear) == 0) "" else uiState.application?.schoolYear.toString(),
            label = { Text("School Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onYearUpdate
        )

        TextField(
            value = uiState.application?.requestType ?: "",
            label = { Text("Request Type (e.g. Food, Housing)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onValueChange = onRequestTypeUpdate
        )


        if (uiState.academicData != null) {
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

        Button(onClick = onIsDataUpdate) {
            Text("Application Documents")
        }

        if (uiState.error != null) {
            Text(text = uiState.error.asString(), modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationViewDataPreview() {
    SocialStoreTheme {
        val uiState = ApplicationState2(
            isLoading = false,
            error = null,
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

        ApplicationViewContent(
            modifier = Modifier,
            uiState = uiState,
            onNameUpdate = {},
            onYearUpdate = {},
            onBirthDateUpdate = {},
            onCcUpdate = {},
            onPhoneUpdate = {},
            onRequestTypeUpdate = {},
            onTypeCourseUpdate = {},
            onCourseUpdate = {},
            onStudentNumberUpdate = {},
            onAddFiles = {},
            onRemoveFile = {},
            onRemoveSubmitedFile = {},
            onSubmitDocument = {},
            onIsDataUpdate = {}
        )
    }
}