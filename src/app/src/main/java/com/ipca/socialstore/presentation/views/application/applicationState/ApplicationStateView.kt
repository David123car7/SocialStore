package com.ipca.socialstore.presentation.views.application.applicationState

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.ui.SocialStoreScaffoldContent
import com.ipca.socialstore.presentation.ui.components.ButtonTracedComponent
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.getFileNameFromUri
import com.ipca.socialstore.presentation.views.application.status.TimelineLine

@Composable
fun ApplicationStateView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val applicationStateViewModel: ApplicationStateViewModel = hiltViewModel()
    val uiState by applicationStateViewModel.uiState

    val context = LocalContext.current

    ApplicationStateViewContent(
        modifier = modifier,
        uiState = uiState,
        onAddSelectedFile = {folderName, uri ->
            applicationStateViewModel.addFiles(
                uri = uri,
                folderName = folderName,
                context = context
            )
        },
        onDeleteFile = { doc, uri, folderName ->
            applicationStateViewModel.removeFile(uri = uri, document = doc, folderName = folderName)
        },
        onSubmitFile = {folderName -> applicationStateViewModel.submitFiles(folderName = folderName, context = context)}
    )

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error!!.asString(context = context), Toast.LENGTH_SHORT).show()
            applicationStateViewModel.clearError()
        }
    }
}

@Composable
fun ApplicationStateViewContent(
    modifier: Modifier,
    uiState: ApplicationState,
    onDeleteFile:(document: DocumentReceiverModel?, uri: Uri?, folderName: String) -> Unit,
    onAddSelectedFile:(folderName: String, uri: Uri?) -> Unit,
    onSubmitFile:(folderName: String) -> Unit){
    var isDataExpanded by remember { mutableStateOf(false) }
    var isDocsExpanded by remember { mutableStateOf(false) }

    val bankStatementsFilesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onAddSelectedFile(DocumentType.BANK_STATEMENTS.folderName, uri)
    }

    val incomeProofFilesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onAddSelectedFile(DocumentType.INCOME_PROOF.folderName, uri)
    }

    val otherIncomeFilesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onAddSelectedFile(DocumentType.OTHER_INCOME.folderName, uri)
    }

    val permanentExpensesFilesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onAddSelectedFile(DocumentType.PERMANENT_EXPENSES.folderName, uri)
    }

    val internationalSupportFilesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onAddSelectedFile(DocumentType.INTERNATIONAL_SUPPORT.folderName, uri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Fundo cinza suave
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StatusTimelineHeader(state = uiState.applicationState?.state ?: "")
        //AlertComponent(show = true, title = "Eliminar Ficheiro", message = "Kazzio", onConfirm = {}, onDismiss = {})
        ExpandableSection(
            title = "Meus Dados",
            icon = Icons.Outlined.Person,
            isExpanded = isDataExpanded,
            onExpandChange = { isDataExpanded = it }
        ) {
            CategoryBox(title = "Dados Pessoais", bgColor = Color.White) {
                ReadOnlyField("Nome Completo", uiState.application?.name ?: "")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { ReadOnlyField("Email", uiState.application?.email ?: "") }
                    Box(Modifier.weight(1f)) { ReadOnlyField(label = "Ano Letivo", value = uiState.application?.schoolYear.toString())}
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { ReadOnlyField(label = "CC", value = uiState.application?.cc ?: "") }
                    Box(Modifier.weight(1f)) { ReadOnlyField(label = "Data Nasc.", value = uiState.application?.birthDate ?:"") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) {ReadOnlyField("Numero de Telemovel", uiState.application?.phoneNumber ?: "")}
                    Box(Modifier.weight(1f)) {ReadOnlyField("Tipo de Pedido", uiState.application?.requestType ?: "")}
                }
            }
            if(uiState.academicData != null){
                CategoryBox(title = "Dados Académicos", bgColor = Color.White) {
                    ReadOnlyField("Curso", uiState.academicData.course)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) { ReadOnlyField(label = "Tipo de Curso", value = uiState.academicData.typeCourse) }
                        Box(Modifier.weight(1f)) { ReadOnlyField(label = "Numero de Estudante", value = uiState.academicData.studenNumber)}
                    }
                }
            }
        }

        ExpandableSection(
            title = "Meus Documentos",
            icon = Icons.Outlined.Person,
            isExpanded = isDocsExpanded,
            onExpandChange = { isDocsExpanded = it }
        ) {
            var bankStatementsBgColor = Color.White
            var bankStatementsTittle = "Extratos Bancários"
            var addBankStatementButton = true
            if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                bankStatementsBgColor = Color(0x120FFC0B)
                bankStatementsTittle += " (Completo)"
                addBankStatementButton = false
            }
            DocumentsList(
                documentsList = uiState.documentsBankStatements,
                files = uiState.selectedBankStatements,
                fileType = DocumentType.BANK_STATEMENTS.folderName,
                tittle = bankStatementsTittle,
                bgColor = bankStatementsBgColor,
                showAddFileButton = addBankStatementButton,
                filePicker = bankStatementsFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )

            var incomeProofBgColor = Color.White
            var incomeProofTittle = "Comprovativos de Rendimento"
            var addIncomeProofButton = true
            if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                incomeProofBgColor = Color(0x120FFC0B)
                incomeProofTittle += " (Completo)"
                addIncomeProofButton = false
            }
            DocumentsList(
                documentsList = uiState.documentsIncomeProof,
                files = uiState.selectedIncomeProof,
                fileType = DocumentType.INCOME_PROOF.folderName,
                tittle = incomeProofTittle,
                bgColor = incomeProofBgColor,
                showAddFileButton = addIncomeProofButton,
                filePicker = incomeProofFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )

            var otherIncomeBgColor = Color.White
            var otherIncomeTittle = "Outros Rendimentos"
            var addOtherIncomeButton = true
            if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                otherIncomeBgColor = Color(0x120FFC0B)
                otherIncomeTittle += " (Completo)"
                addOtherIncomeButton = false
            }
            DocumentsList(
                documentsList = uiState.documentsOtherIncome,
                files = uiState.selectedOtherIncome,
                tittle = otherIncomeTittle,
                fileType = DocumentType.OTHER_INCOME.folderName,
                bgColor = otherIncomeBgColor,
                showAddFileButton = addOtherIncomeButton,
                filePicker = otherIncomeFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )

            var permanentExpensesBgColor = Color.White
            var permanentExpensesTittle = "Despesas Permanentes"
            var addPermanentExpensesButton = true
            if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                permanentExpensesBgColor = Color(0x120FFC0B)
                permanentExpensesTittle += " (Completo)"
                addPermanentExpensesButton = false
            }
            DocumentsList(
                documentsList = uiState.documentsPermanentExpenses,
                files = uiState.selectedPermanentExpenses,
                tittle = permanentExpensesTittle,
                fileType = DocumentType.PERMANENT_EXPENSES.folderName,
                bgColor = permanentExpensesBgColor,
                showAddFileButton = addPermanentExpensesButton,
                filePicker = permanentExpensesFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )
            var internationalSupportBgColor = Color.White
            var internationalSupportTittle = "Apoio Internacional"
            var addInternationalSupportButton = true
            if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                internationalSupportBgColor = Color(0x120FFC0B)
                internationalSupportTittle += " (Completo)"
                addInternationalSupportButton = false
            }
            DocumentsList(
                documentsList = uiState.documentsInternationalSupport,
                files = uiState.selectedInternationalSupport,
                tittle = internationalSupportTittle,
                fileType = DocumentType.INTERNATIONAL_SUPPORT.folderName,
                bgColor = internationalSupportBgColor,
                showAddFileButton = addInternationalSupportButton,
                filePicker = internationalSupportFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )
        }
    }
}


@Composable
fun DocumentsList(
    documentsList: List<DocumentReceiverModel>,
    files: List<Uri>,
    tittle: String,
    fileType: String,
    bgColor: Color,
    showAddFileButton: Boolean,
    filePicker: ManagedActivityResultLauncher<String, Uri?>,
    onDeleteFile:(document: DocumentReceiverModel?, uri: Uri?, folderName: String) -> Unit,
    onSubmitFile:(folderName: String) -> Unit){
    CategoryBox(title = tittle, bgColor = bgColor) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espaço entre documentos
        ) {
            if (documentsList.isNotEmpty())  {
                documentsList.forEach { doc ->
                    var statusColor: Color
                    var icon : ImageVector
                    val status: String
                    var canDelete = false

                    if(doc.state == DocumentStatus.ACCEPTED.status){
                        statusColor = Color.Green
                        icon = Icons.Default.Star
                        status = "Aceite"
                    }
                    else if(doc.state == DocumentStatus.TO_REVIEW.status){
                        statusColor = Color.Gray
                        icon = Icons.Default.Star
                        status = "Por Rever"
                    }
                    else{
                        statusColor = Color.Red
                        icon = Icons.Default.Star
                        status = "Não Aceite"
                        canDelete = true
                    }

                    DocumentRow(
                        fileName = doc.name,
                        status = status,
                        statusColor = statusColor,
                        bgColor = Color(0xFFF5F7FA),
                        date = doc.createdAt,
                        imageVector = icon,
                        canDelete = canDelete,
                        onDeleteFile = { onDeleteFile(doc, null, DocumentType.BANK_STATEMENTS.folderName) },
                        errorMessage = doc.description,
                    )
                }
            }

            if(files.isNotEmpty()){
                files.forEach { file ->
                    DocumentRow(
                        fileName = getFileNameFromUri(LocalContext.current, uri = file),
                        status = "Por Enviar",
                        statusColor = Color.Magenta,
                        bgColor = Color(0xFFF5F7FA),
                        canDelete = true,
                        onDeleteFile = { onDeleteFile(null, file, DocumentType.BANK_STATEMENTS.folderName) },
                        imageVector = Icons.Default.Star,
                    )
                }
            }
            if(showAddFileButton)
                ButtonTracedComponent(modifier = Modifier,label = "Adicionar Ficheiros", color = Color.Gray, onClick = {filePicker.launch("application/pdf")})
            if(files.isNotEmpty())
                BotaoUpload(onClick = {onSubmitFile(fileType)})
        }
    }
}

@Composable
fun StatusTimelineHeader(state: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if(state == ApplicationStatus.CORRECTION.status) BorderStroke(1.dp, Color.Black) else BorderStroke(1.dp, Color.LightGray), //depois corrigir a cor
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if(state == ApplicationStatus.PENDING.status){
                TimelineComponent(
                    label = "Submetido",
                    description = "A candidatura foi submetida.",
                    icon = Icons.Default.Star,
                    step = 1,
                    color = Color(0xFFFBC02D)
                )
            }
            else if(state == ApplicationStatus.CORRECTION.status){
                TimelineComponent(
                    label = "Correção",
                    description = "A candidatura precisa de ser corrigida.",
                    icon = Icons.Default.Star,
                    step = 2,
                    color = Color(0xFF1976D2)
                )
            }
            else if(state == ApplicationStatus.APPROVED.status){
                TimelineComponent(
                    label = "Aceite",
                    description = "Candidatura aprovada.",
                    icon = Icons.Default.Star,
                    step = 3,
                    color = Color(0xFF43A047)
                )
            }
            else if(state == ApplicationStatus.REJECTED.status){
                TimelineComponent(
                    label = "Recusado",
                    description = "Candidatura não aceite.",
                    icon = Icons.Default.Star,
                    step = 3,
                    color = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
fun TimelineComponent(label: String, description: String, icon: ImageVector, step: Int, color: Color){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Info, null, tint = color)
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Estado: $label", fontWeight = FontWeight.Bold, color = color)
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        }
    }
    Spacer(Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimelineStep("1", "Submetido", true, step >= 1, color)
        TimelineLine(step >= 2, color)
        TimelineStep("2", "Em Análise", step >= 2, step > 2, color)
        TimelineLine(step >= 3, color)
        TimelineStep("3", "Decisão", step >= 3, step == 3, color)
    }
}

@Composable
fun TimelineStep(number: String, label: String, isActive: Boolean, isCompleted: Boolean, activeColor: Color) {
    val color = if (isActive) activeColor else Color(0xFFE0E0E0)
    val bgColor = if (isActive) activeColor else Color.Transparent
    val textColor = if (isActive) Color.White else Color(0xFF9E9E9E)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(2.dp, color, CircleShape)
        ) {
            if (isCompleted) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            else Text(number, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (isActive) Color.Black else Color.Gray)
    }
}

@Composable
fun CategoryBox(
    title: String,
    icon: ImageVector? = null,
    bgColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                //Icon(icon, null, tint = Color(0xFF455A64), modifier = Modifier.size(20.dp))
                //Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF263238))
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun DocumentRow(
    fileName: String,
    date: String? = null,
    status: String,
    statusColor: Color,
    bgColor: Color,
    canDelete: Boolean,
    imageVector: ImageVector,
    onDeleteFile:() -> Unit,
    errorMessage: String? = null
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

            if(canDelete){
                IconButton(
                    onClick = onDeleteFile,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar ficheiro",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(20.dp)
                    )
                }
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


        if (!errorMessage.isNullOrEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, null, tint = statusColor, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(errorMessage, color = statusColor, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DocumentRowState(status: String, imageVector: ImageVector, color: Color){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(status, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BotaoUpload(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF878787),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = "Enviar Ficheiros",
        )
    }
}

@Composable
fun ReadOnlyField(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(label, fontSize = 11.sp, color = Color(0xFF78909C), fontWeight = FontWeight.Bold)
        Text(value, fontSize = 14.sp, color = Color(0xFF263238))
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationStatePreview(){
    SocialStoreTheme {
        val mockDocumentsList = listOf(
            // CASO 1: Documento Aceite (Verde)
            DocumentReceiverModel(
                id = 1,
                path = "https://exemplo.com/docs/cc_frente.jpg",
                name = "Cartao_Cidadao_Frente.jpg",
                folderName = "Identificação",
                createdAt = "2024-12-10",
                stateId = 1,
                applicationId = 123,
                state = DocumentStatus.ACCEPTED.status,
                description = "",
                appDocId = 1,
            )
        )
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
            documentsBankStatements = mockDocumentsList,
            academicData = AcademicModel(typeCourse = "", course = "", studenNumber = "")
        )

        SocialStoreScaffoldContent(navController = rememberNavController(), userRole = UserRole.DEFAULT, logout = {}) { paddingValues ->
            ApplicationStateViewContent(
                modifier = Modifier.padding(paddingValues = paddingValues),
                uiState = uiState, onAddSelectedFile = { folderName, uri -> {}}, onDeleteFile = {folderName, uri, document -> {}}, onSubmitFile = {})
        }
    }
}