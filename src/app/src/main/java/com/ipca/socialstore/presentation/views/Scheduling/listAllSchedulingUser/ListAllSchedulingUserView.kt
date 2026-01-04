package com.ipca.socialstore.presentation.views.Scheduling.listAllSchedulingUser


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel


@Composable
fun ListAllSchedulingUserView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : ListAllSchedulingUserViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.fetchInfo()
    }
    ListAllSchedulingUserContent(
        modifier = modifier,
        uiState = uiState,
        onClickAccept = {viewModel.selectListAccept()},
        onClickHistory = {viewModel.selectListHistory()},
        onClickCancel = {viewModel.selectListCanceled()}
    )
}
@Composable
fun ListAllSchedulingUserContent(
    modifier: Modifier,
    uiState : ListSchedulingUserState,
    onClickAccept : () -> Unit,
    onClickHistory : () -> Unit,
    onClickCancel : () -> Unit,
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = uiState.beneficiary?.name ?: "Carregando...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Processo nº ${uiState.beneficiary?.id ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AssistChip(onClick = {onClickAccept()}, label = { Text("Próximos (${uiState.accept})") })

            AssistChip(onClick = {onClickHistory()}, label = { Text("Histórico") })

            AssistChip(onClick = {onClickCancel()}, label = { Text("Cancelado(${uiState.cancel})") })
        }

        Text(
            text = "Cronograma",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(uiState.showList) { index, item ->
                SchedulingTimelineItem(
                    item = item,
                    isLast = index == uiState.scheduling.lastIndex
                )
            }
        }
    }
}

@Composable
fun SchedulingTimelineItem(
    item: SchedulingModel,
    isLast: Boolean
) {

    val isAccept = item.state == "accept"
    val statusColor = if (isAccept) Color(0xFF4CAF50) else Color(0xFFEF5350)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {

        Card(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .clickable(onClick = {/*funcao que recebe o estado(just/confir -> rencaminha dif paginas*/}),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.schedulingDate,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = if (isAccept) "Entrega de Agendada" else "Entrega Cancelada",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    color = if (isAccept) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isAccept) "Confirmar" else "Justificar",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = statusColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListAllSchedulingUserPreview() {
    val mockBeneficiary = BeneficiaryModel(
        id = 3,
        name = "David",
        createdAt = "2024-01-01",
        birthDate = "1990-05-15",
        addressId = 1
    )

    val mockScheduling = listOf(
        SchedulingModel(id = 6, schedulingDate = "2026-01-24", beneficiaryId = 3, state = "accept"),
        SchedulingModel(id = 2, schedulingDate = "2026-01-22", beneficiaryId = 3, state = "canceled"),
        SchedulingModel(id = 4, schedulingDate = "2026-01-14", beneficiaryId = 3, state = "canceled")
    )

    val mockUiState = ListSchedulingUserState(
        beneficiary = mockBeneficiary,
        scheduling = mockScheduling,
        accept = 1,
        cancel = 2
    )

    MaterialTheme {
        ListAllSchedulingUserContent(
            modifier = Modifier,
            uiState = mockUiState,
            onClickHistory = {},
            onClickAccept = {},
            onClickCancel = {}
        )
    }
}