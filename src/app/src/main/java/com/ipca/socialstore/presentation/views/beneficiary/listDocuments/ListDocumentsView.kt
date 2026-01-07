package com.ipca.socialstore.presentation.views.beneficiary.listDocuments

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToDrive
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.ui.components.IntroductionComponent
import com.ipca.socialstore.presentation.ui.theme.IconTint
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox
import com.ipca.socialstore.presentation.views.application.applicationStateAdmin.ApplicationStateAdminViewModel

@Composable
fun ListDocumentsView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel: ListDocumentsViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    ListDocumentsContent(
        modifier = modifier,
        uiState = uiState,
        onDownloadFile = {fileName, filePath -> viewModel.downloadDocument(filePath = filePath, fileName = fileName)},
    )

    val context = LocalContext.current

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            viewModel.saveToUserSelectedUri(uri)
        } else {
            Toast.makeText(context, "Gravação cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.downloadEvent.collect { event ->
            when (event) {
                is ListDocumentsViewModel.DownloadEvent.Loading -> {
                    Toast.makeText(context, "A descarregar...", Toast.LENGTH_SHORT).show()
                }
                is ListDocumentsViewModel.DownloadEvent.PromptUserToSave -> {
                    saveFileLauncher.launch(event.fileName)
                }
                is ListDocumentsViewModel.DownloadEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ListDocumentsViewModel.DownloadEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LaunchedEffect(uiState.error) {
        if(uiState.error != null){
            Toast.makeText(context, uiState.error!!.asString(context = context), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState.hasApplication) {
        if(!uiState.hasApplication){
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = BeneficiaryRoutes.Home
            )
        }
    }
}

@Composable
fun ListDocumentsContent(
    modifier: Modifier,
    uiState: ListDocumentsState,
    onDownloadFile:(fileName: String, filePath: String)->Unit){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        IntroductionComponent(
            tittle = "Os Meus Documentos",
        )

        Documents(
            documentsList = uiState.documentsBankStatements,
            tittle = "Extratos Bancários",
            bgColor = Color.White,
            onDownloadFile = { fileName, path -> onDownloadFile(fileName, path)}
        )

        Documents(
            documentsList = uiState.documentsIncomeProof,
            tittle = "Comprovativos de Rendimento",
            bgColor = Color.White,
            onDownloadFile = { fileName, path -> onDownloadFile(fileName, path)}
        )

        Documents(
            documentsList = uiState.documentsOtherIncome,
            tittle = "Outros Rendimentos",
            bgColor = Color.White,
            onDownloadFile = { fileName, path -> onDownloadFile(fileName, path)}
        )

        Documents(
            documentsList = uiState.documentsPermanentExpenses,
            tittle = "Despesas Permanentes",
            bgColor = Color.White,
            onDownloadFile = { fileName, path -> onDownloadFile(fileName, path)}
        )

        Documents(
            documentsList = uiState.documentsInternationalSupport,
            tittle = "Apoio Internacional",
            bgColor = Color.White,
            onDownloadFile = { fileName, path -> onDownloadFile(fileName, path)}
        )
    }
}

@Composable
fun Documents(
    documentsList: List<DocumentReceiverModel>,
    onDownloadFile:(fileName: String, filePath: String)->Unit,
    tittle: String,
    bgColor: Color
){
    CategoryBox(
        title = tittle,
        bgColor = bgColor,
        description = "",
        descriptionTextColor = Color.Black,
        descriptionBgTextColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            documentsList.forEach { doc ->
                SimpleDocumentCard(
                    fileName = doc.name,
                    date = doc.createdAt,
                    bgColor = bgColor,
                    onDownload = {onDownloadFile(doc.name, doc.path)}
                )
            }
        }
    }
}

@Composable
fun SimpleDocumentCard(
    fileName: String,
    date: String? = null,
    bgColor: Color,
    onDownload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Spacer(Modifier.width(4.dp))

                Column {
                    Text(
                        text = fileName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (date != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Submetido em $date",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            IconButton(
                onClick = onDownload
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Descarregar",
                    tint = IconTint
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun ListDocumentsPreview(){
    val uiState = ListDocumentsState(
        isLoading = false,
        error = null,
    )
    ListDocumentsContent(
        modifier = Modifier,
        uiState = uiState,
        onDownloadFile = { fileName, path ->}
    )
}

@Preview(showBackground = true)
@Composable()
fun SimpleDocumentCardPreview(){
    SimpleDocumentCard(
        "Kazzio",
        date = "data",
        bgColor = Color.White,
        onDownload = {}
    )
}

