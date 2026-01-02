package com.ipca.socialstore.presentation.views.application.listApplications

import android.R
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.ui.components.AlertComponent
import com.ipca.socialstore.presentation.ui.components.AlertInputComponent
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.components.ReadOnlyField
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.components.WarningComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox
import com.ipca.socialstore.presentation.views.application.applicationState.DocumentRow

@Composable
fun ListApplicationsView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel: ListApplicationsViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    ListApplicationsContent(
        modifier = modifier,
        uiState = uiState,
        onDenyData = { id, msg -> viewModel.updateApplicationDataState(id = id, message = msg)}
    )
}

@Composable
fun ListApplicationsContent(modifier: Modifier, uiState: ListApplicationsState, onDenyData: (Int, String) -> Unit) {
    var appSelected by remember { mutableStateOf<ApplicationModelReceiver?>(null) }
    var isDataExpanded by remember { mutableStateOf(false) }
    var isDocsExpanded by remember { mutableStateOf(false) }

    Crossfade(targetState = appSelected, label = "PageTransition") { selectedApp ->
        if (selectedApp != null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F7FA))
            ) {
                Surface(
                    color = Color.White,
                    shadowElevation = 4.dp, // Adds a subtle shadow to separate header
                    modifier = Modifier.fillMaxWidth().zIndex(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { appSelected = null }) {
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
                            application = appSelected!!,
                            onSubmitMessage = onDenyData,
                            bgColor = Color.White
                        )
                    }

                    ExpandableSection(
                        title = "Documentos da candidatura",
                        icon = Icons.Outlined.Person,
                        isExpanded = isDocsExpanded,
                        onExpandChange = { isDocsExpanded = !isDocsExpanded }
                    ) {
                        //ApplicationDocuments()
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.applications) { application ->
                    CandidateCard(
                        candidateName = application.name,
                        createdAt = application.createdAt,
                        status = application.applicationState.state,
                        bgColor = getApplicationBGColor(applicationStatus = application.applicationState.state) ?: Color.White,
                        textColor = getApplicationTextColor(applicationStatus = application.applicationState.state) ?: Color.White,
                        onDetailsClick = { appSelected = application },
                    )
                }
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
    CategoryBox(title = "Dados Pessoais", bgColor = bgColor) {
        if (application.applicationDataState.state == ApplicationDataStatus.DENIED.status) {
            WarningComponent(tittle = "Mensagem Enviada.", message = application.applicationDataState.message ?: "", icon = Icons.Default.Warning)
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
    fileType: String,
    bgColor: Color,
    onDownloadFile:()->Unit){
    CategoryBox(title = tittle, bgColor = bgColor) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espaço entre documentos
        ) {
            documentsList.forEach { doc ->
                var statusColor: Color
                var icon: ImageVector
                val status: String
                var canDelete = false

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
                    canDelete = true
                }
            }
        }
    }
}

@Composable
fun DocumentCard(fileName: String,
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

@Composable
fun CandidateCard(
    candidateName: String,
    createdAt: String,
    status: String,
    bgColor: Color,
    textColor: Color,
    onDetailsClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GreenIPCA), // A borda verde característica
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = candidateName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = status,
                        color = textColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver detalhes", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun CandidateCardPreview(){
    Column() {
        CandidateCard(
            candidateName = "David Amorim Carvalho",
            createdAt = "19/08/2025",
            status = "Por Aceitar",
            bgColor = Color(0xFFFFCCC7),
            textColor = Color(0xFFCF1322),
            onDetailsClick = {},
        )
    }
}

fun getApplicationBGColor(applicationStatus: String): Color?{
    if(applicationStatus == ApplicationStatus.APPROVED.status)
        return Color(0xFFD6F5D6)
    else if(applicationStatus == ApplicationStatus.REJECTED.status)
        return Color(0xFFFFCCC7)
    else if(applicationStatus == ApplicationStatus.PENDING.status)
        return Color(0xFFFFEebb)
    return null
}

fun getApplicationTextColor(applicationStatus: String): Color?{
    if(applicationStatus == ApplicationStatus.APPROVED.status)
        return Color(0xFF237804)
    else if(applicationStatus == ApplicationStatus.REJECTED.status)
        return Color(0xFFCF1322)
    else if(applicationStatus == ApplicationStatus.PENDING.status)
        return Color(0xFFD48806)
    return null
}

