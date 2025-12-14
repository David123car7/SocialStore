package com.ipca.socialstore.presentation.views.application.applicationData

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun ApplicationView(modifier: Modifier){
    val applicationViewModel: ApplicationViewModel = hiltViewModel()
    val uiState by applicationViewModel.uiState
    val context = LocalContext.current

    ApplicationViewContent(
        modifier = modifier,
        uiState = uiState,
        onIsDataUpdate = applicationViewModel::updateIsData,
        onNameUpdate = applicationViewModel::updateName,
        onYearUpdate = applicationViewModel::updateSchoolYear,
        onBirthDateUpdate = applicationViewModel::updateBirthDate,
        onCcUpdate = applicationViewModel::updateCc,
        onPhoneUpdate = applicationViewModel::updatePhoneNumber,
        onRequestTypeUpdate = applicationViewModel::updateRequestType,
        onTypeCourseUpdate = applicationViewModel::updateTypeCourse,
        onCourseUpdate = applicationViewModel::updateCourse,
        onStudentNumberUpdate = applicationViewModel::updateStudentNumber,
        onRemoveSubmitedFile = {doc -> applicationViewModel.removeSubmittedDocument(document = doc)},
        onAddFiles = {uris -> applicationViewModel.addFiles(uris = uris, folderName = DocumentType.BANK_STATEMENTS.folderName)},
        onRemoveFile = {uri -> applicationViewModel.removeSelectedFile(uri = uri, folderName = DocumentType.BANK_STATEMENTS.folderName)},
        onSubmitDocument = {applicationViewModel.submitFiles(folderName = DocumentType.BANK_STATEMENTS.folderName, context = context)}
    )
}

@Composable
fun ApplicationViewContent(
    modifier: Modifier = Modifier,
    uiState: ApplicationState,
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
    onAddFiles: (List<Uri>) -> Unit,
    onRemoveSubmitedFile: (DocumentModel) -> Unit,
    onRemoveFile: (Uri) -> Unit,
    onSubmitDocument: () -> Unit
) {
    if(uiState.isData){
        ApplicationViewDataContent(
            modifier = modifier,
            uiState = uiState,
            onNameUpdate = onNameUpdate,
            onYearUpdate = onYearUpdate,
            onBirthDateUpdate = onBirthDateUpdate,
            onCcUpdate = onCcUpdate,
            onPhoneUpdate = onPhoneUpdate,
            onRequestTypeUpdate = onRequestTypeUpdate,
            onTypeCourseUpdate = onTypeCourseUpdate,
            onCourseUpdate = onCourseUpdate,
            onStudentNumberUpdate = onStudentNumberUpdate,
            onIsDataUpdate = onIsDataUpdate
        )
    }
    else{
        ApplicationDocumentsViewContent(
            modifier = modifier,
            uiState = uiState,
            onAddFiles = onAddFiles,
            onRemoveFile = onRemoveFile,
            onSubmitDocument = onSubmitDocument,
            onRemoveSubmitedFile = onRemoveSubmitedFile,
            onIsDataUpdate = onIsDataUpdate)
    }

    if (uiState.error != null) {
        Text(text = uiState.error.asString(), modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationPreview() {
    SocialStoreTheme {
        val uiState = ApplicationState(
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
            onIsDataUpdate = {},
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
        )
    }
}