package com.ipca.socialstore.presentation.views.application.applicationData

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.presentation.utils.getFileNameFromUri
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun ApplicationDocumentsViewContent(
    modifier: Modifier = Modifier,
    uiState: ApplicationState,
    onIsDataUpdate: () -> Unit,

    onAddFiles: (List<Uri>) -> Unit,
    onRemoveSubmitedFile: (DocumentModel) -> Unit,
    onRemoveFile: (Uri) -> Unit,
    onSubmitDocument: () -> Unit
) {

    val multiFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        onAddFiles(uris)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState), // <--- Critical for long forms
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedButton(
            onClick = { multiFilePicker.launch("application/pdf") }, // Filter for PDFs
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Attach Documents (PDF)")
        }

        if (uiState.documentsBankStatements.isNotEmpty()) {
            Text(
                text = "Submited Documents:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 8.dp)
            ) {
                items(uiState.documentsBankStatements) { doc ->
                    DocumentRowItem(document = doc, onRemoveSubmitedFile)
                }
            }
        }

        if (uiState.selectedBankStatements.isNotEmpty()) {
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
                items(uiState.selectedBankStatements) { uri ->
                    FileRowItem(uri = uri, onRemoveFile = { onRemoveFile(uri) })
                }
            }
        }

        Button(
            onClick = {
                onSubmitDocument()
            }
        ){
            Text("Submit")
        }

        Button(onClick = onIsDataUpdate) {
            Text("Application Data")
        }

        if (uiState.error != null) {
            Text(text = uiState.error.asString(), modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun DocumentRowItem(document: DocumentModel, onRemoveSubmitedFile: (DocumentModel) -> Unit,) {
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

            Text(
                text = document.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )


            Button(
                onClick = { onRemoveSubmitedFile(document) },
                modifier = Modifier.size(24.dp)
            ) {
            }
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

            Button(
                onClick = { onRemoveFile(uri) },
                modifier = Modifier.size(24.dp)
            ) {
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ApplicationDocumentsPreview() {
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

        ApplicationDocumentsViewContent(
            modifier = Modifier,
            uiState = uiState,

            onAddFiles = {},
            onRemoveFile = {},
            onRemoveSubmitedFile = {},
            onSubmitDocument = {},
            onIsDataUpdate = {}
        )
    }
}