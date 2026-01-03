package com.ipca.socialstore.presentation.views.application.applicationStateAdmin

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.ui.components.AlertInputComponent
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.components.ReadOnlyField
import com.ipca.socialstore.presentation.ui.components.WarningComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox
import com.ipca.socialstore.presentation.views.application.listApplications.ListApplicationsViewModel
import kotlin.collections.forEach

@Composable
fun AplicationStateAdminView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel: ApplicationStateAdminViewModel = hiltViewModel()
    val uiState by viewModel.uiState
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
                is ApplicationStateAdminViewModel.DownloadEvent.Loading -> {
                    Toast.makeText(context, "A descarregar...", Toast.LENGTH_SHORT).show()
                }
                is ApplicationStateAdminViewModel.DownloadEvent.PromptUserToSave -> {
                    saveFileLauncher.launch(event.fileName)
                }
                is ApplicationStateAdminViewModel.DownloadEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ApplicationStateAdminViewModel.DownloadEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    ApplicationStateAdminContent(
        modifier = modifier,
        uiState = uiState,
        onDenyData = { id, msg -> viewModel.updateApplicationDataState(id = id, message = msg)},
        onDownloadFile = {fileName, filePath -> viewModel.downloadDocument(filePath = filePath, fileName = fileName)}
    )
}

@Composable
fun ApplicationStateAdminContent(
    modifier: Modifier,
    uiState: ApplicationAdminState,
    onDenyData: (Int, String) -> Unit,
    onDownloadFile:(fileName: String, filePath: String)->Unit){
    var isDataExpanded by remember { mutableStateOf(false) }
    var isDocsExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Voltar",
                        tint = GreenIPCA
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalhes da Candidatura",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ExpandableSection(
                title = "Dados da candidatura",
                icon = Icons.Outlined.Person,
                isExpanded = isDataExpanded,
                onExpandChange = { isDataExpanded = !isDataExpanded }
            ) {
                ApplicationData(
                    application = uiState.application,
                    onSubmitMessage = onDenyData,
                    bgColor = Color.White
                )
            }

            val documentsCompletedColor = Color(0x120FFC0B)
            val documentsWrongColor = Color(0x1BFF0000)

            ExpandableSection(
                title = "Documentos da candidatura",
                icon = Icons.Outlined.Person,
                isExpanded = isDocsExpanded,
                onExpandChange = { isDocsExpanded = !isDocsExpanded }
            ) {
                var bankStatementsBgColor = Color.White
                var bankStatementsTittle = "Extratos Bancários"
                if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    bankStatementsBgColor = documentsCompletedColor
                    bankStatementsTittle += " (Completo)"
                }
                else if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    bankStatementsBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsBankStatements,
                    tittle = bankStatementsTittle,
                    bgColor = bankStatementsBgColor,
                    onDownloadFile = onDownloadFile
                )

                // --- 2. COMPROVATIVOS DE RENDIMENTO ---
                var incomeProofBgColor = Color.White
                var incomeProofTittle = "Comprovativos de Rendimento"
                if(uiState.incomeProofDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    incomeProofBgColor = documentsCompletedColor
                    incomeProofTittle += " (Completo)"
                }
                else if(uiState.incomeProofDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    incomeProofBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsIncomeProof,
                    tittle = incomeProofTittle,
                    bgColor = incomeProofBgColor,
                    onDownloadFile = onDownloadFile
                )

                var otherIncomeBgColor = Color.White
                var otherIncomeTittle = "Outros Rendimentos"
                if(uiState.otherIncomeDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    otherIncomeBgColor = documentsCompletedColor
                    otherIncomeTittle += " (Completo)"
                }
                else if(uiState.otherIncomeDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    otherIncomeBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsOtherIncome,
                    tittle = otherIncomeTittle,
                    bgColor = otherIncomeBgColor,
                    onDownloadFile = onDownloadFile
                )

                var permanentExpensesBgColor = Color.White
                var permanentExpensesTittle = "Despesas Permanentes"
                if(uiState.permanentExpensesDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    permanentExpensesBgColor = documentsCompletedColor
                    permanentExpensesTittle += " (Completo)"
                }
                else if(uiState.permanentExpensesDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    permanentExpensesBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsPermanentExpenses,
                    tittle = permanentExpensesTittle,
                    bgColor = permanentExpensesBgColor,
                    onDownloadFile = onDownloadFile
                )

                var internationalSupportBgColor = Color.White
                var internationalSupportTittle = "Apoio Internacional"
                if(uiState.internationalSupportDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    internationalSupportBgColor = documentsCompletedColor
                    internationalSupportTittle += " (Completo)"
                }
                else if(uiState.internationalSupportDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    internationalSupportBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsInternationalSupport,
                    tittle = internationalSupportTittle,
                    bgColor = internationalSupportBgColor,
                    onDownloadFile = onDownloadFile
                )
            }
        }
    }
}

@Composable
fun ApplicationData(
    application: ApplicationModelReceiver,
    bgColor: Color,
    onSubmitMessage: (Int, String) -> Unit,
) {
    var showAlertBox by remember { mutableStateOf(false) }
    AlertInputComponent(
        show = showAlertBox,
        label = "candidatura",
        title = "Não Aceitar",
        message = "Escreve o motivo por estes dados não serem aceites.",
        onConfirm = { msg -> onSubmitMessage(application.applicationDataState.id!!, msg)},
        onDismiss = {showAlertBox = false}
    )
    CategoryBox(title = "Dados Pessoais", description = "Dados Aceites", descriptionColor = GreenIPCA,bgColor = bgColor) {
        if (application.applicationDataState.state == ApplicationDataStatus.DENIED.status) {
            WarningComponent(
                tittle = "Mensagem Enviada.",
                message = application.applicationDataState.message ?: "",
                bgColor = Color(0xFFFFCCC7),
                mainColor = Color(0xFFCF1322),
                icon = Icons.Default.Warning
            )
        }
        if (application.applicationDataState.state == ApplicationDataStatus.ACCEPTED.status) {
            WarningComponent(
                tittle = "Dados Aceites",
                message = "",
                bgColor = Color(0x120FFC0B),
                mainColor = GreenIPCA,
                icon = Icons.Default.Warning
            )
        }
        ReadOnlyField("Nome Completo", application.name)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                ReadOnlyField("Email", application.email)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                ReadOnlyField("CC", application.cc)
            }
            Box(Modifier.weight(1f)) {
                ReadOnlyField("Data Nasc.", application.birthDate)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                ReadOnlyField("Telemóvel", application.phoneNumber)
            }
            Box(Modifier.weight(1f)) {
                ReadOnlyField("Tipo", application.requestType)
            }
        }

        if(application.applicationDataState.state == ApplicationDataStatus.TO_REVIEW.status){
            HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(GreenIPCA)
                    ) {
                        Text("Aceitar")
                    }
                    Button(
                        onClick = { showAlertBox = true },
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(GreenIPCA)
                    ) {
                        Text("Rejeitar")
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationDocuments(
    documentsList: List<DocumentReceiverModel>,
    tittle: String,
    bgColor: Color,
    onDownloadFile:(fileName: String, filePath: String)->Unit){
    CategoryBox(title = tittle, bgColor = bgColor) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            documentsList.forEach { doc ->
                var statusColor: Color
                var icon: ImageVector
                val status: String

                if (doc.state == DocumentStatus.ACCEPTED.status) {
                    statusColor = Color.Green
                    icon = Icons.Default.Star
                    status = "Aceite"
                } else if (doc.state == DocumentStatus.TO_REVIEW.status) {
                    statusColor = Color.Gray
                    icon = Icons.Default.Star
                    status = "Por Rever"
                } else {
                    statusColor = Color.Red
                    icon = Icons.Default.Star
                    status = "Não Aceite"
                }

                DocumentCard(
                    fileName = doc.name,
                    date = doc.createdAt,
                    status = status,
                    statusColor = statusColor,
                    bgColor = bgColor,
                    imageVector = icon,
                    onDownloadFile = { onDownloadFile(doc.name, doc.path) }
                )
            }
        }
    }
}

@Composable
fun DocumentCard(
    fileName: String,
    date: String? = null,
    status: String,
    statusColor: Color,
    bgColor: Color,
    imageVector: ImageVector,
    onDownloadFile:() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(fileName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Black)
                    if(date != null)
                        Text("Submetido em $date", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = Color.Gray)
                }
            }

            IconButton(
                onClick = onDownloadFile,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "downloadFile",
                    tint = GreenIPCA,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}


@Preview(showBackground = true)
@Composable()
fun ApplicationStateAdminPreview(){

}