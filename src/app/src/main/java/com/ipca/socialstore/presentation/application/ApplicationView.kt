package com.ipca.socialstore.presentation.application

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipca.socialstore.presentation.utils.getFileNameFromUri
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun ApplicationView(modifier: Modifier){
    val applicationViewModel: ApplicationViewModel = hiltViewModel()
    val uiState by applicationViewModel.uiState

    ApplicationViewContent(
        modifier = modifier,
        uiState = uiState,
        onSubmitApplication = { applicationViewModel.submitFile() },
        onAddFiles = {
            value -> applicationViewModel.addFiles(value)
        },
        onRemoveFile = { value -> applicationViewModel.removeFile(value)}
    )
}

@Composable
fun ApplicationViewContent(
    modifier: Modifier,
    uiState: ApplicationState,
    onSubmitApplication:()->Unit,
    onAddFiles:(uri: List<Uri>?)->Unit,
    onRemoveFile:(uri: Uri?)->Unit){

    val multiFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        onAddFiles(uris)
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Beneficiary Application",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Please fill in your details and attach proof of enrollment/income.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        OutlinedButton(
            onClick = { multiFilePicker.launch("application/pdf") }, // Filter for PDFs
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
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
                    .heightIn(max = 200.dp) // Limit height so it scrolls if many files
                    .padding(top = 8.dp)
            ) {
                items(uiState.selectedFiles) { uri ->
                    FileRowItem(uri = uri, onRemoveFile = { onRemoveFile(uri) })
                }
            }
        }

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
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove file",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ApplicationPreview(){
    SocialStoreTheme() {
        val uiState = ApplicationState(isLoading = false, error = null, isSuccess = false, selectedFiles = emptyList())
        ApplicationViewContent(modifier = Modifier, uiState = uiState , onSubmitApplication = { Unit}, onAddFiles = { uri -> null}, onRemoveFile = { Unit})
    }
}