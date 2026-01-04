package com.ipca.socialstore.presentation.views.application.applicationStateAdmin

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.ui.components.AlertComponent
import com.ipca.socialstore.presentation.ui.components.AlertInputComponent
import com.ipca.socialstore.presentation.ui.components.ReadOnlyField
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox
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

    LaunchedEffect(uiState.error) {
        if(uiState.error != null){
            Toast.makeText(context, uiState.error!!.asString(context = context), Toast.LENGTH_SHORT).show()
        }
    }

    ApplicationStateAdminContent(
        modifier = modifier,
        uiState = uiState,
        onDenyData = {msg -> viewModel.updateApplicationDataState(state = ApplicationDataStatus.DENIED.status,message = msg)},
        onDownloadFile = {fileName, filePath -> viewModel.downloadDocument(filePath = filePath, fileName = fileName)},
        goBack = {navController.popBackStack()},
        onAcceptApplicationData = {viewModel.updateApplicationDataState(state = ApplicationDataStatus.ACCEPTED.status,message = "")},
        onUpdateDocState = { doc, folderName,state, msg ->
            if(doc != null){
                if(state){
                    viewModel.updateDocumentState(documentId = doc.stateId, folderName = folderName, state = DocumentStatus.ACCEPTED.status, message = msg)
                }
                else{
                    viewModel.updateDocumentState(documentId = doc.id!!, folderName = folderName, state = DocumentStatus.DENIED.status, message = msg)
                    viewModel.updateApplicationState(state = ApplicationStates.CORRECTION.status)
                }
            }
        },
        onUpdateTypeDocState = { id, state, type, msg ->
            if(state){
                viewModel.updateApplicationDocumentTypeState(id = id, state = ApplicationDocumentTypeState.COMPLETED.state, type =type ,message = msg)
            }
            else{
                viewModel.updateApplicationDocumentTypeState(id = id, state = ApplicationDocumentTypeState.SOMETHING_WRONG.state, type =type ,message = msg)
                viewModel.updateApplicationState(state = ApplicationStates.CORRECTION.status)
            }
        },
        onDenyApplication = {},
        onAcceptApplication = {}
    )
}

@Composable
fun ApplicationStateAdminContent(
    modifier: Modifier,
    uiState: ApplicationAdminState,
    goBack:() -> Unit,
    onDenyData: (String) -> Unit,
    onAcceptApplicationData:() -> Unit,
    onUpdateDocState:(doc: DocumentReceiverModel?, folderName: String, state: Boolean, msg: String) -> Unit,
    onUpdateTypeDocState:(docId: Int, state: Boolean, type: String,msg: String) -> Unit,
    onDownloadFile:(fileName: String, filePath: String)->Unit,
    onDenyApplication:() -> Unit,
    onAcceptApplication:() -> Unit){
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
                IconButton(onClick = goBack) {
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
            var showAlertAcceptDocument by remember { mutableStateOf(false) }
            var showAlertDenyDocument by remember { mutableStateOf(false) }
            var documentSelected by remember { mutableStateOf<DocumentReceiverModel?>(null) }

            AlertComponent(
                show = showAlertAcceptDocument,
                title = "Aceitar",
                icon = Icons.Filled.CheckBox,
                color = GreenIPCA,
                message = "De certeza que queres aceitar este documento?",
                onConfirm = {onUpdateDocState(documentSelected, documentSelected!!.folderName ,true, "")},
                onDismiss = {showAlertAcceptDocument = false}
            )

            AlertInputComponent(
                show = showAlertDenyDocument,
                title = "Não Aceitar",
                icon = Icons.Filled.Warning,
                color = Color(0xFFCF1322),
                message = "Escreve o motivo por este documento não ser aceito.",
                onConfirm = { msg -> onUpdateDocState(documentSelected, documentSelected!!.folderName,false, msg )},
                onDismiss = {showAlertDenyDocument = false}
            )

            ApplicationData(
                application = uiState.application,
                onSubmitMessage = onDenyData,
                onAcceptApplicationData = onAcceptApplicationData
            )

            ApplicationDocuments(
                documentsList = uiState.documentsBankStatements,
                documentsState = uiState.bankStatementDocsState,
                tittle = "Extratos Bancários",
                bgColor = Color.White,
                onDownloadFile = onDownloadFile,
                onUpdateDocState = { doc, state ->
                    documentSelected = doc
                    if(state) showAlertAcceptDocument = true
                    else showAlertDenyDocument = true
                },
                onUpdateTypeDocState = { id, state, type,msg ->
                    onUpdateTypeDocState(id, state, type, msg)
                }
            )

            ApplicationDocuments(
                documentsList = uiState.documentsIncomeProof,
                documentsState = uiState.incomeProofDocsState,
                tittle = "Comprovativos de Rendimento",
                bgColor = Color.White,
                onDownloadFile = onDownloadFile,
                onUpdateDocState = { doc, state ->
                    documentSelected = doc
                    if(state) showAlertAcceptDocument = true
                    else showAlertDenyDocument = true
                },
                onUpdateTypeDocState = { id, state, type,msg ->
                    onUpdateTypeDocState(id, state, type, msg)
                }
            )

            ApplicationDocuments(
                documentsList = uiState.documentsOtherIncome,
                documentsState = uiState.otherIncomeDocsState,
                tittle = "Outros Rendimentos",
                bgColor = Color.White,
                onDownloadFile = onDownloadFile,
                onUpdateDocState = { doc, state ->
                    documentSelected = doc
                    if(state) showAlertAcceptDocument = true
                    else showAlertDenyDocument = true
                },
                onUpdateTypeDocState = { id, state, type,msg ->
                    onUpdateTypeDocState(id, state, type, msg)
                }
            )

            ApplicationDocuments(
                documentsList = uiState.documentsPermanentExpenses,
                documentsState = uiState.permanentExpensesDocsState,
                tittle = "Despesas Permanentes",
                bgColor = Color.White,
                onDownloadFile = onDownloadFile,
                onUpdateDocState = { doc, state ->
                    documentSelected = doc
                    if(state) showAlertAcceptDocument = true
                    else showAlertDenyDocument = true
                },
                onUpdateTypeDocState = { id, state, type,msg ->
                    onUpdateTypeDocState(id, state, type, msg)
                }
            )

            ApplicationDocuments(
                documentsList = uiState.documentsInternationalSupport,
                documentsState = uiState.internationalSupportDocsState,
                tittle = "Apoio Internacional",
                bgColor = Color.White,
                onDownloadFile = onDownloadFile,
                onUpdateDocState = { doc, state ->
                    documentSelected = doc
                    if(state) showAlertAcceptDocument = true
                    else showAlertDenyDocument = true
                },
                onUpdateTypeDocState = { id, state, type,msg ->
                    onUpdateTypeDocState(id, state, type, msg)
                }
            )
        }
    }
}

@Composable
fun ApplicationData(
    application: ApplicationModelReceiver,
    onSubmitMessage: (String) -> Unit,
    onAcceptApplicationData:() -> Unit,
) {
    var showAlertDenyDataBox by remember { mutableStateOf(false) }
    var showAlertAcceptDataBox by remember { mutableStateOf(false) }

    AlertInputComponent(
        show = showAlertDenyDataBox,
        title = "Não Aceitar",
        icon = Icons.Filled.Warning,
        color = Color(0xFFCF1322),
        message = "Escreve o motivo por estes dados não serem aceites.",
        onConfirm = { msg -> onSubmitMessage(msg)},
        onDismiss = {showAlertDenyDataBox = false}
    )

    AlertComponent(
        show = showAlertAcceptDataBox,
        title = "Aceitar",
        icon = Icons.Filled.CheckBox,
        color = GreenIPCA,
        message = "De certeza que queres aceitar esta candidatura?",
        onConfirm = onAcceptApplicationData,
        onDismiss = {showAlertAcceptDataBox = false}
    )

    var dataCategoryDesc: String = ""
    var dataCategoryDescTextColor: Color = Color.Black
    var dataCategoryDescBgTextColor: Color = Color.White
    if(application.applicationDataState.state == ApplicationDataStatus.ACCEPTED.status){
        dataCategoryDesc = "Dados Aceites"
        dataCategoryDescTextColor = GreenIPCA
        dataCategoryDescBgTextColor = Color(0x120FFC0B)
    }
    if(application.applicationDataState.state == ApplicationDataStatus.DENIED.status){
        dataCategoryDesc = "Dados Negados"
        dataCategoryDescTextColor = Color(0xFFCF1322)
        dataCategoryDescBgTextColor = Color(0x1BFF0000)
    }
    if(application.applicationDataState.state == ApplicationDataStatus.TO_REVIEW.status){
        dataCategoryDesc = "Por Rever"
        dataCategoryDescTextColor = Color(0xFFDAA210)
        dataCategoryDescBgTextColor = Color(0x43DAA210)
    }

    CategoryBox(
        title = "Dados Pessoais",
        description = dataCategoryDesc,
        descriptionTextColor = dataCategoryDescTextColor,
        descriptionBgTextColor = dataCategoryDescBgTextColor) {
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
                        onClick = {showAlertAcceptDataBox = true},
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
                        onClick = { showAlertDenyDataBox = true },
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
    documentsState: ApplicationDocumentTypeModel,
    tittle: String,
    bgColor: Color,
    onUpdateDocState:(doc: DocumentReceiverModel, state: Boolean)-> Unit,
    onUpdateTypeDocState:(id: Int,state: Boolean, type: String, msg: String)-> Unit,
    onDownloadFile:(fileName: String, filePath: String)->Unit){

    var showAlertDenyDocsBox by remember { mutableStateOf(false) }
    var showAlertAcceptDocsBox by remember { mutableStateOf(false) }

    AlertInputComponent(
        show = showAlertDenyDocsBox,
        title = "Não Aceitar",
        icon = Icons.Filled.Warning,
        color = Color(0xFFCF1322),
        message = "Escreve o motivo por estes documentos não serem aceites.",
        onConfirm = { msg -> onUpdateTypeDocState(documentsState.id!!,false, documentsState.type,msg)},
        onDismiss = {showAlertDenyDocsBox = false}
    )

    AlertComponent(
        show = showAlertAcceptDocsBox,
        title = "Aceitar",
        icon = Icons.Filled.CheckBox,
        color = GreenIPCA,
        message = "De certeza que queres aceitar estes documentos?",
        onConfirm = {onUpdateTypeDocState(documentsState.id!!,true, documentsState.type,"")},
        onDismiss = {showAlertAcceptDocsBox = false}
    )

    var docCategoryDesc: String = ""
    var docCategoryDescTextColor: Color = Color.Black
    var docCategoryDescBgTextColor: Color = Color.White
    if(documentsState.state == ApplicationDocumentTypeState.COMPLETED.state){
        docCategoryDesc = "Completo"
        docCategoryDescTextColor = GreenIPCA
        docCategoryDescBgTextColor = Color(0x120FFC0B)
    }
    if(documentsState.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
        docCategoryDesc = "Incorreto"
        docCategoryDescTextColor = Color(0xFFCF1322)
        docCategoryDescBgTextColor = Color(0x1BFF0000)
    }
    if(documentsState.state == ApplicationDocumentTypeState.TO_REVIEW.state){
        docCategoryDesc = "Por Rever"
        docCategoryDescTextColor = Color(0xFFDAA210)
        docCategoryDescBgTextColor = Color(0x43DAA210)
    }

    CategoryBox(
        title = tittle,
        bgColor = bgColor,
        description = docCategoryDesc,
        descriptionTextColor = docCategoryDescTextColor,
        descriptionBgTextColor = docCategoryDescBgTextColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            documentsList.forEach { doc ->
                var statusColor: Color
                var icon: ImageVector
                val status: String
                var showUpdateButtons: Boolean

                if (doc.state == DocumentStatus.ACCEPTED.status) {
                    statusColor = Color.Green
                    icon = Icons.Default.Star
                    status = "Aceite"
                    showUpdateButtons = false
                } else if (doc.state == DocumentStatus.TO_REVIEW.status) {
                    statusColor = Color.Gray
                    icon = Icons.Default.Star
                    status = "Por Rever"
                    showUpdateButtons = true
                } else {
                    statusColor = Color.Red
                    icon = Icons.Default.Star
                    status = "Não Aceite"
                    showUpdateButtons = false
                }

                DocumentCard(
                    fileName = doc.name,
                    date = doc.createdAt,
                    status = status,
                    statusColor = statusColor,
                    bgColor = bgColor,
                    imageVector = icon,
                    showUpdateButtons = showUpdateButtons,
                    onDownloadFile = { onDownloadFile(doc.name, doc.path)},
                    onAcceptFile = { onUpdateDocState(doc, true)},
                    onDenyFile = {onUpdateDocState(doc, false)}
                )
            }
            if(documentsState.state == ApplicationDocumentTypeState.TO_REVIEW.state){
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = {showAlertAcceptDocsBox = true},
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
                            onClick = { showAlertDenyDocsBox = true },
                            modifier = Modifier
                                .padding(5.dp)
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFCF1322))
                        ) {
                            Text("Rejeitar")
                        }
                    }
                }
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
    showUpdateButtons: Boolean,
    onDownloadFile: () -> Unit,
    onAcceptFile: () -> Unit,
    onDenyFile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top, 
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp) // Botões compactos
            ) {
                IconButton(
                    onClick = onDownloadFile,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if(showUpdateButtons){
                    IconButton(
                        onClick = onDenyFile,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Rejeitar",
                            tint = Color(0xFFFF5252), // Vermelho Suave
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onAcceptFile,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Aceitar",
                            tint = Color(0xFF4CAF50), // Verde Material Design
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = status,
                color = statusColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview(showBackground = true)
@Composable()
fun ApplicationStateAdminPreview(){
    DocumentCard(
        fileName = "Kazzio.pfg",
        date = "12/02/2026",
        status = "to_review",
        statusColor = GreenIPCA,
        bgColor = Color.White,
        imageVector = Icons.Filled.Check,
        onDownloadFile = {},
        onAcceptFile = {},
        showUpdateButtons = true,
        onDenyFile = {}
    )
}