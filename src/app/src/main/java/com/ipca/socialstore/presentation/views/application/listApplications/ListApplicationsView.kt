package com.ipca.socialstore.presentation.views.application.listApplications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.components.IntroductionComponent
import com.ipca.socialstore.presentation.ui.components.SearchBarContent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.utils.ui.getApplicationStateViewData

@Composable
fun ListApplicationsView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel: ListApplicationsViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    ListApplicationsContent(
        modifier = modifier,
        uiState = uiState,
        onAppSelected = {app ->
            val routeName = AdminRoutes.ApplicationState::class.qualifiedName!!
            navController.navigate("$routeName/${app.id}")
        }
    )
}

@Composable
fun ListApplicationsContent(
    modifier: Modifier,
    uiState: ListApplicationsState,
    onAppSelected:(ApplicationModelReceiver) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(uiState.applications, searchQuery) {
        uiState.applications.filter { application ->
            application.name.contains(searchQuery, ignoreCase = true)
        }
    }
    Column(
        modifier = modifier.padding(top = 18.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        IntroductionComponent(
            tittle = "Candidaturas"
        )

        SearchBarContent(
            modifier = Modifier.padding(15.dp),
            onSearchItem = { query -> searchQuery = query }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredList) { application ->
                val ui = getApplicationStateViewData(state = application.applicationState.state)
                CandidateCard(
                    candidateName = application.name,
                    createdAt = application.createdAt,
                    status = ui.text,
                    statusBgColor = ui.bgColor,
                    statusTextColor = ui.textColor,
                    onDetailsClick = { onAppSelected(application) },
                )
            }
        }
    }
}

@Composable
fun CandidateCard(
    candidateName: String,
    createdAt: String,
    status: String,
    statusBgColor: Color,
    statusTextColor: Color,
    onDetailsClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GreenIPCA),
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
                    color = statusBgColor,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = status,
                        color = statusTextColor,
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
            statusBgColor = Color(0xFFFFCCC7),
            statusTextColor = Color(0xFFCF1322),
            onDetailsClick = {},
        )
    }
}

fun getApplicationBGColor(applicationStatus: String): Color?{
    if(applicationStatus == ApplicationStates.APPROVED.status)
        return Color(0xFFD6F5D6)
    else if(applicationStatus == ApplicationStates.REJECTED.status)
        return Color(0xFFFFCCC7)
    else if(applicationStatus == ApplicationStates.PENDING.status)
        return Color(0xFFFFEebb)
    return null
}

fun getApplicationTextColor(applicationStatus: String): Color?{
    if(applicationStatus == ApplicationStates.APPROVED.status)
        return Color(0xFF237804)
    else if(applicationStatus == ApplicationStates.REJECTED.status)
        return Color(0xFFCF1322)
    else if(applicationStatus == ApplicationStates.PENDING.status)
        return Color(0xFFD48806)
    return null
}

