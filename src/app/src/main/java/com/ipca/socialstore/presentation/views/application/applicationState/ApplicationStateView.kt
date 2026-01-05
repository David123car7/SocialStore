package com.ipca.socialstore.presentation.views.application.applicationState

import ApplicationForm
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.ui.SocialStoreScaffoldContent
import com.ipca.socialstore.presentation.ui.components.AlertComponent
import com.ipca.socialstore.presentation.ui.components.ButtonTracedComponent
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.components.ReadOnlyField
import com.ipca.socialstore.presentation.ui.components.WarningComponent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.files.getFileNameFromUri
import com.ipca.socialstore.presentation.utils.ui.getApplicationDataStateViewData
import com.ipca.socialstore.presentation.utils.ui.getApplicationDocumentTypeStateViewData

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
        onNameUpdate = applicationStateViewModel::updateName,
        onYearUpdate = applicationStateViewModel::updateSchoolYear,
        onBirthDateUpdate = applicationStateViewModel::updateBirthDate,
        onCcUpdate = applicationStateViewModel::updateCc,
        onPhoneUpdate = applicationStateViewModel::updatePhoneNumber,
        onCourseUpdate = applicationStateViewModel::updateCourse,
        onTypeCourseUpdate = applicationStateViewModel::updateTypeCourse,
        onStudentNumberUpdate = applicationStateViewModel::updateStudentNumber,
        onRequestTypeUpdate = applicationStateViewModel::updateRequestType,
        onDeleteFile = { doc, uri, folderName ->
            applicationStateViewModel.removeFile(uri = uri, document = doc, folderName = folderName)
        },
        onSubmitFile = { docTypeStateId, type, msg, folderName ->
            applicationStateViewModel.submitFile(
                folderName = folderName,
                type = type,
                msg = msg,
                docTypeStateId = docTypeStateId,
                context = context
            )
        },
        onDeleteApplication = { applicationStateViewModel.deleteApplication()},
        onApplicationUpdate = {
            applicationStateViewModel.updateApplication()
        }
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
    onDeleteApplication:() -> Unit,
    onSubmitFile:(docTypeStateId: Int, type: String, msg: String, folderName: String) -> Unit,
    onNameUpdate: (String) -> Unit,
    onYearUpdate: (String) -> Unit,
    onBirthDateUpdate: (String) -> Unit,
    onCcUpdate: (String) -> Unit,
    onPhoneUpdate: (String) -> Unit,
    onRequestTypeUpdate: (String) -> Unit,
    onTypeCourseUpdate: (String) -> Unit,
    onCourseUpdate: (String) -> Unit,
    onStudentNumberUpdate: (String) -> Unit,
    onApplicationUpdate:() ->Unit,
    ){

    var isDataExpanded by remember { mutableStateOf(true) }
    var isDocsExpanded by remember { mutableStateOf(false) }
    var showAlertBox by remember { mutableStateOf(false) }

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
            .background(Color(0xFFF5F7FA))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StatusTimelineHeader(state = uiState.application.applicationState.state)

        AlertComponent(
            show = showAlertBox,
            title = "Eliminar Candidatura",
            icon = Icons.Default.Warning,
            color = Color(0xFFFF5252),
            message = "Tens a certeza que queres eliminar o ficheiro?",
            onConfirm = {onDeleteApplication()},
            onDismiss = {showAlertBox = false}
        )

        ExpandableSection(
            title = "Meus Dados",
            icon = Icons.Outlined.Person,
            isExpanded = isDataExpanded,
            onExpandChange = { isDataExpanded = it }
        ) {
            if (uiState.application.applicationDataState.state == ApplicationDataStatus.DENIED.status){
                WarningComponent(
                    tittle = "Correção Necessária.",
                    message = uiState.application.applicationDataState.message ?: "",
                    bgColor = Color(0xFFFFCCC7),
                    mainColor = Color(0xFFCF1322),
                    icon = Icons.Default.Warning
                )
            }
            val uiData = getApplicationDataStateViewData(uiState.application.applicationDataState.state)
            if (uiState.application.applicationDataState.state != ApplicationDataStatus.DENIED.status) {
                CategoryBox(
                    title = "Dados Pessoais",
                    description = uiData.text,
                    descriptionTextColor = uiData.textColor,
                    descriptionBgTextColor = uiData.bgColor
                ) {
                    ReadOnlyField("Nome Completo", uiState.application.name)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            ReadOnlyField(
                                "Email",
                                uiState.application.email
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            ReadOnlyField(
                                label = "Ano Letivo",
                                value = uiState.application.schoolYear.toString()
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            ReadOnlyField(
                                label = "CC",
                                value = uiState.application.cc
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            ReadOnlyField(
                                label = "Data Nasc.",
                                value = uiState.application.birthDate
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            ReadOnlyField(
                                "Numero de Telemovel",
                                uiState.application.phoneNumber
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            ReadOnlyField(
                                "Tipo de Pedido",
                                uiState.application.requestType
                            )
                        }
                    }
                }
                if(uiState.application.academicData != null){
                    CategoryBox(
                        title = "Dados Académicos",
                        description = uiData.text,
                        descriptionTextColor = uiData.textColor,
                        descriptionBgTextColor = uiData.bgColor
                    ) {
                        ReadOnlyField("Curso", uiState.application.academicData.course)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(1f)) { ReadOnlyField(label = "Tipo de Curso", value = uiState.application.academicData.typeCourse) }
                            Box(Modifier.weight(1f)) { ReadOnlyField(label = "Numero de Estudante", value = uiState.application.academicData.studenNumber)}
                        }
                    }
                }
            }
            else {
                CategoryBox(
                    title = "Dados Pessoais",
                    description = uiData.text,
                    descriptionTextColor = uiData.textColor,
                    descriptionBgTextColor = uiData.bgColor
                ) {
                    ApplicationForm(
                        modifier = Modifier,
                        name = uiState.application.name,
                        birthDate = uiState.application.birthDate,
                        cc = uiState.application.cc,
                        phoneNumber = uiState.application.phoneNumber,
                        schoolYear = if (uiState.application.schoolYear == 0) "" else uiState.application.schoolYear.toString(),
                        requestType = uiState.application.requestType,
                        isStudent = uiState.application.academicData != null,
                        academicCourseType = uiState.application.academicData?.typeCourse ?: "",
                        academicCourseName = uiState.application.academicData?.course ?: "",
                        academicStudentNumber = uiState.application.academicData?.studenNumber ?: "",
                        onNameChange = onNameUpdate,
                        onBirthDateChange = onBirthDateUpdate,
                        onCcChange = onCcUpdate,
                        onPhoneChange = onPhoneUpdate,
                        onYearChange = onYearUpdate,
                        onRequestTypeChange = onRequestTypeUpdate,
                        onAcademicTypeChange = onTypeCourseUpdate,
                        onAcademicCourseChange = onCourseUpdate,
                        onAcademicNumberChange = onStudentNumberUpdate,
                        isAdmin = true,
                        onIsStudentUpdate = {}
                    )
                }
                Button(
                    onClick = onApplicationUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continuar", fontWeight = FontWeight.Bold)
                }
            }
        }
        ExpandableSection(
            title = "Meus Documentos",
            icon = Icons.Outlined.Person,
            isExpanded = isDocsExpanded,
            onExpandChange = { isDocsExpanded = it }
        ) {
            DocumentsList(
                documentsList = uiState.documentsBankStatements,
                documentsState = uiState.bankStatementDocsState,
                files = uiState.selectedBankStatements,
                tittle = "Extratos Bancários",
                description = uiState.bankStatementDocsState.description,
                bgColor = Color.White,
                filePicker = bankStatementsFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )

            DocumentsList(
                documentsList = uiState.documentsIncomeProof,
                documentsState = uiState.incomeProofDocsState,
                files = uiState.selectedIncomeProof,
                tittle = "Comprovativos de Rendimento",
                description = uiState.incomeProofDocsState.description,
                bgColor = Color.White,
                filePicker = incomeProofFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )

            DocumentsList(
                documentsList = uiState.documentsOtherIncome,
                documentsState = uiState.otherIncomeDocsState,
                files = uiState.selectedOtherIncome,
                tittle = "Outros Rendimentos",
                description = uiState.otherIncomeDocsState.description,
                bgColor = Color.White,
                filePicker = otherIncomeFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )

            DocumentsList(
                documentsList = uiState.documentsPermanentExpenses,
                documentsState = uiState.permanentExpensesDocsState,
                files = uiState.selectedPermanentExpenses,
                tittle = "Despesas Permanentes",
                description = uiState.permanentExpensesDocsState.description,
                bgColor = Color.White,
                filePicker = permanentExpensesFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )

            DocumentsList(
                documentsList = uiState.documentsInternationalSupport,
                documentsState = uiState.internationalSupportDocsState,
                files = uiState.selectedInternationalSupport,
                tittle = "Apoio Internacional",
                description = uiState.internationalSupportDocsState.description,
                bgColor = Color.White,
                filePicker = internationalSupportFilesPicker,
                onDeleteFile = onDeleteFile,
                onSubmitFile = onSubmitFile
            )
        }
        Button(
            onClick = {showAlertBox = true},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(6.dp),
        ) {
            Text(
                text = "Eliminar Candidatura",
            )
        }
    }
}

@Composable
fun DocumentsList(
    documentsList: List<DocumentReceiverModel>,
    documentsState: ApplicationDocumentTypeModel,
    files: List<Uri>,
    tittle: String,
    description: String? = null,
    bgColor: Color,
    filePicker: ManagedActivityResultLauncher<String, Uri?>,
    onDeleteFile:(document: DocumentReceiverModel?, uri: Uri?, folderName: String) -> Unit,
    onSubmitFile:(docTypeStateId: Int, type: String, msg: String, folderName: String) -> Unit){

    val uiData = getApplicationDocumentTypeStateViewData(documentsState.state)

    CategoryBox(
        title = tittle,
        bgColor = bgColor,
        description = uiData.text,
        descriptionTextColor = uiData.textColor,
        descriptionBgTextColor = uiData.bgColor
    ) {
        if(documentsState.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state) {
            WarningComponent(
                tittle = "Correção Necessária.",
                message = documentsState.description,
                bgColor = Color(0xFFFFCCC7),
                mainColor = Color(0xFFCF1322),
                icon = Icons.Default.Warning
            )
        }

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
                        onDeleteFile = { onDeleteFile(doc, null, documentsState.type) },
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
                        onDeleteFile = { onDeleteFile(null, file, documentsState.type) },
                        imageVector = Icons.Default.Star,
                    )
                }
            }
            if(documentsState.type != ApplicationDocumentTypeState.COMPLETED.state)
                ButtonTracedComponent(modifier = Modifier,label = "Adicionar Ficheiros", color = Color.Gray, onClick = {filePicker.launch("application/pdf")})
            if(files.isNotEmpty()){
                Button(
                    onClick = { onSubmitFile(documentsState.id!!, documentsState.type, "", documentsState.type) },
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
        }
    }
}

@Composable
fun StatusTimelineHeader(state: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if(state == ApplicationStates.CORRECTION.status) BorderStroke(1.dp, Color.Black) else BorderStroke(1.dp, Color.LightGray), //depois corrigir a cor
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if(state == ApplicationStates.PENDING.status){
                TimelineComponent(
                    label = "Submetido",
                    description = "A candidatura foi submetida.",
                    icon = Icons.Default.Star,
                    step = 1,
                    color = Color(0xFFFBC02D)
                )
            }
            else if(state == ApplicationStates.CORRECTION.status){
                TimelineComponent(
                    label = "Correção",
                    description = "A candidatura precisa de ser corrigida.",
                    icon = Icons.Default.Star,
                    step = 2,
                    color = Color(0xFF1976D2)
                )
            }
            else if(state == ApplicationStates.APPROVED.status){
                TimelineComponent(
                    label = "Aceite",
                    description = "Candidatura aprovada.",
                    icon = Icons.Default.Star,
                    step = 3,
                    color = Color(0xFF43A047)
                )
            }
            else if(state == ApplicationStates.REJECTED.status){
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
        TimelineStep("2", "Em Análise", step >= 2, step > 2, color)
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
    description: String? = null,
    descriptionTextColor: Color = Color.Black,
    descriptionBgTextColor: Color = Color.White,
    bgColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth() // Ensure Row fills width
            ) {
                // 1. Title takes available space (weight 1f) and truncates if needed
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF263238),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if(description != null){
                    // 2. Surface keeps its natural size, pushed to the right
                    Surface(
                        modifier = Modifier.padding(start = 15.dp),
                        color = descriptionBgTextColor,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(
                            text = description,
                            color = descriptionTextColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1, // Optional: ensure status doesn't break lines
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE)) // Updated to HorizontalDivider for Material3
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

@Preview(showBackground = true)
@Composable
fun ApplicationStatePreview(){
    SocialStoreTheme {
        val mockDocumentsList = listOf(
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
            application = createEmptyApplication(),
            documentsBankStatements = mockDocumentsList,
        )

        SocialStoreScaffoldContent(navController = rememberNavController(), userRole = UserRole.DEFAULT, logout = {}) { paddingValues ->
            ApplicationStateViewContent(
                modifier = Modifier.padding(paddingValues = paddingValues),
                uiState = uiState,
                onAddSelectedFile = { folderName, uri -> {}},
                onDeleteFile = {folderName, uri, document -> {}},
                onSubmitFile = {docTypeStateId, type,msg, folderName  -> {}},
                onDeleteApplication = {},
                onNameUpdate = {},
                onYearUpdate = {},
                onBirthDateUpdate = {},
                onCcUpdate = {},
                onPhoneUpdate = {},
                onRequestTypeUpdate = {},
                onTypeCourseUpdate = {},
                onCourseUpdate = {},
                onStudentNumberUpdate = {},
                onApplicationUpdate = {}
            )
        }
    }
}