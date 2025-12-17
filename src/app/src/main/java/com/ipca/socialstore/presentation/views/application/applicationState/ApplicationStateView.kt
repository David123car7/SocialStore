package com.ipca.socialstore.presentation.views.application.applicationState

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.SocialStoreScaffoldContent
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.views.application.applicationData.ApplicationState2
import com.ipca.socialstore.presentation.views.application.status.TimelineLine
import com.ipca.socialstore.presentation.views.stock.List.SingleItemStock

@Composable
fun ApplicationStateView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val applicationStateViewModel: ApplicationStateViewModel = hiltViewModel()
    val uiState by applicationStateViewModel.uiState

    ApplicationStateViewContent(modifier = modifier, uiState = uiState)
}

@Composable
fun ApplicationStateViewContent(modifier: Modifier, uiState: ApplicationState){
    var isDataExpanded by remember { mutableStateOf(false) }
    var isDocsExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Fundo cinza suave
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        StatusTimelineHeader(state = uiState.applicationState?.state ?: "")

        ExpandableSection(
            title = "Meus Dados",
            icon = Icons.Outlined.Person,
            isExpanded = isDataExpanded,
            onExpandChange = { isDataExpanded = it }
        ) {
            CategoryBox(title = "Dados Pessoais") {
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
                CategoryBox(title = "Dados Académicos") {
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
            CategoryBox(title = "Extratos Bancários") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Espaço entre documentos
                ) {
                    if (uiState.documentsBankStatements.isEmpty()) {
                        Text("Nenhum documento submetido.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        uiState.documentsBankStatements.forEach { doc ->
                            var statusColor: Color
                            var icon : ImageVector
                            val status: String

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
                            }

                            DocumentRow(
                                fileName = doc.name,
                                status = status,
                                statusColor = statusColor,
                                bgColor = Color(0xFFF5F7FA),
                                date = doc.created_at,
                                imageVector = icon,
                                errorMessage = doc.description,
                            )
                        }
                    }
                }
            }
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
            modifier = Modifier.size(28.dp).clip(CircleShape).background(bgColor).border(2.dp, color, CircleShape)
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
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
    date: String,
    status: String,
    statusColor: Color,
    bgColor: Color,
    imageVector: ImageVector,
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
                    Text("Submetido em $date", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = Color.Gray)
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
                created_at = "2024-12-10",
                stateId = 1,
                applicationId = 123,
                state = DocumentStatus.ACCEPTED.status,
                description = ""
            ),

            // CASO 2: Documento em Análise (Cinza)
            DocumentReceiverModel(
                id = 2,
                path = "https://exemplo.com/docs/irs_2023.pdf",
                name = "Declaracao_IRS_2023.pdf",
                folderName = "Rendimentos",
                created_at = "2024-12-12",
                stateId = 2,
                applicationId = 123,
                state = DocumentStatus.TO_REVIEW.status,
                description = "",
            ),
            DocumentReceiverModel(
                id = 2,
                path = "https://exemplo.com/docs/irs_2023.pdf",
                name = "Kazzio.pdf",
                folderName = "Rendimentos",
                created_at = "2024-12-12",
                stateId = 2,
                applicationId = 123,
                state = DocumentStatus.DENIED.status,
                description = "Nao se ve um caralho no pdf"
            ),
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
                uiState = uiState)
        }
    }
}