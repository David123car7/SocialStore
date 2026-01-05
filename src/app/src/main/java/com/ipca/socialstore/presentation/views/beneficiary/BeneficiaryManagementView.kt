package com.ipca.socialstore.presentation.views.beneficiary

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import kotlin.collections.forEachIndexed

@Composable
fun BeneficiaryManagementView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : BeneficiaryManagementViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(uiState.accept , uiState.error) {
        viewModel.fetchInfo()
    }
    BeneficiaryManagementContent(
        modifier = modifier,
        uiState = uiState
    )

}

@Composable
fun BeneficiaryManagementContent(
    modifier: Modifier,
    uiState: BeneficiaryManagementState
) {
    val beneficiary = uiState.beneficiary ?: return

    var expandedSection by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {

            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = beneficiary.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Processo nº ${beneficiary.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        ManagementSectionCard(
            title = "Agendamentos",
            icon = Icons.Default.DateRange,
            iconColor = Color(0xFFA5D6A7),
            badgeText = "${uiState.accept} Próximos",
            badgeColor = Color(0xFFE8F5E9),
            badgeTextColor = Color(0xFF2E7D32),
            isExpanded = expandedSection == "agendamentos",
            onClick = { expandedSection = if (expandedSection == "agendamentos") null else "agendamentos" }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Agendamentos recentes", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                uiState.scheduling?.take(3)?.forEachIndexed { index, item ->
                    SchedulingItem(
                        date = item.schedulingDate,
                        type = "Entrega Alimentar"
                    )


                    if (index < uiState.scheduling.take(3).lastIndex) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ver Calendário Completo", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        ManagementSectionCard(
            title = "Faltas / Justificações",
            icon = Icons.Default.Warning,
            iconColor = Color(0xFFEF9A9A),
            badgeText = "${uiState.cancel} Pendentes",
            badgeColor = Color(0xFFFFEBEE),
            badgeTextColor = Color(0xFFC62828),
            isExpanded = expandedSection == "faltas",
            onClick = { expandedSection = if (expandedSection == "faltas") null else "faltas" }
        ) {
            Text("Histórico de faltas aqui...", modifier = Modifier.padding(16.dp))
        }

        ManagementSectionCard(
            title = "Pedidos",
            icon = Icons.Default.Notifications,
            iconColor = Color(0xFF90CAF9),
            badgeText = "12 Realizados",
            badgeColor = Color(0xFFE3F2FD),
            badgeTextColor = Color(0xFF1565C0),
            isExpanded = expandedSection == "pedidos",
            onClick = { expandedSection = if (expandedSection == "pedidos") null else "pedidos" }
        ) {
            Text("Lista de pedidos aqui...", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun ManagementSectionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    badgeText: String,
    badgeColor: Color,
    badgeTextColor: Color,
    isExpanded: Boolean,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize() // Animação suave ao abrir/fechar
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6E6E6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = iconColor.copy(alpha = 0.4f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                        Icon(icon, null, tint = iconColor, modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(title, fontWeight = FontWeight.Bold)
                }
                BadgeChip(badgeText, badgeColor, badgeTextColor)
            }

            if (isExpanded && content != null) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

@Composable
fun BadgeChip(text: String, containerColor: Color, contentColor: Color) {
    Surface(color = containerColor, shape = RoundedCornerShape(16.dp)) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SchedulingItem(date: String, type: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("$date - $type", style = MaterialTheme.typography.bodyMedium)
        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SocialStoreTheme {
        val mockBeneficiary = BeneficiaryModel(
            id = 12345,
            name = "Alice Pereira",
            birthDate = "12/05/2003",
            academicId = 1,
            phoneNumber = "dwadawdwa"
        )

        val uiState = BeneficiaryManagementState(
            beneficiary = mockBeneficiary,
            isLoading = false,
            error = null
        )

        BeneficiaryManagementContent(
            modifier = Modifier,
            uiState = uiState
        )
    }
}
