package com.ipca.socialstore.presentation.views.application.listApplications

import android.R
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.components.ReadOnlyField
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox

@Composable
fun ListApplicationsView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel: ListApplicationsViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    ListApplicationsContent(
        modifier = modifier,
        uiState = uiState,
        onSubmitMessage = {

        }
    )
}

@Composable
fun ListApplicationsContent(modifier: Modifier, uiState: ListApplicationsState, onSubmitMessage: (Int) -> Unit) {
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

                ApplicationData(application = appSelected!!, isDataExpanded = isDataExpanded, onDataExpand = {isDataExpanded = !isDataExpanded}, onSubmitMessage = onSubmitMessage )
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
    isDataExpanded: Boolean,
    onDataExpand: () -> Unit,
    onSubmitMessage: (Int) -> Unit,
) {
    var feedbackText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ExpandableSection(
            title = "Meus Dados",
            icon = Icons.Outlined.Person,
            isExpanded = isDataExpanded,
            onExpandChange = { onDataExpand() }
        ) {
            CategoryBox(title = "Dados Pessoais", bgColor = Color.White) {
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

                HorizontalDivider(modifier = Modifier.padding(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.EventNote,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Feedback do Administrador",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextFieldStringComponent(
                        modifier = Modifier,
                        label = "Motivo da Correção",
                        value = feedbackText,
                        onValueUpdate = {feedbackText = it}
                    )

                    Button(
                        onClick = { onSubmitMessage(application.id!!) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(GreenIPCA)
                    ) {
                        Text("Enviar Feedback")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
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

