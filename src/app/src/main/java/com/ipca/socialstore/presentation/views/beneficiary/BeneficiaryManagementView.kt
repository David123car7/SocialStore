package com.ipca.socialstore.presentation.views.beneficiary

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun BeneficiaryManagementView(
    modifier: Modifier,
    navController: NavController
) {
    val viewModel: BeneficiaryManagementViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(uiState.accept, uiState.error) {
        viewModel.fetchInfo()
    }

    // O conteúdo principal que agora inclui o botão no fundo
    BeneficiaryManagementContent(
        modifier = modifier,
        uiState = uiState,
        onAddNewScheduling = {
            val route = AdminRoutes.SchedulingMainPage::class.qualifiedName
            navController.navigate("$route/${uiState.beneficiary?.id}")
        }
    )
}

@Composable
fun BeneficiaryManagementContent(
    modifier: Modifier,
    uiState: BeneficiaryManagementState,
    onAddNewScheduling: () -> Unit
) {
    val beneficiary = uiState.beneficiary ?: return
    var expandedSection by remember { mutableStateOf<String?>(null) }

    // Estado de scroll para permitir chegar ao botão no fundo
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .verticalScroll(scrollState) // Ativa o scroll vertical
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- Cabeçalho ---
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

        // --- Secção: Agendamentos ---
        ManagementSectionCard(
            title = "Agendamentos",
            icon = Icons.Default.DateRange,
            iconColor = Color(0xFFA5D6A7),
            badgeText = "${uiState.accept ?: 0} Próximos",
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

                uiState.scheduling?.filter { it.state == "accept" }?.take(3)?.forEachIndexed { index, item ->
                    SchedulingItem(date = item.schedulingDate)
                    if (index < 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Navegação para calendário */ },
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ver Calendário Completo", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // --- Secção: Faltas ---
        ManagementSectionCard(
            title = "Faltas / Justificações",
            icon = Icons.Default.Warning,
            iconColor = Color(0xFFEF9A9A),
            badgeText = "${uiState.cancel ?: 0} Pendentes",
            badgeColor = Color(0xFFFFEBEE),
            badgeTextColor = Color(0xFFC62828),
            isExpanded = expandedSection == "faltas",
            onClick = { expandedSection = if (expandedSection == "faltas") null else "faltas" }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Faltas", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                uiState.scheduling?.filter { it.state == "canceled" || it.state == "justified" }?.take(3)?.forEachIndexed { index, item ->
                    SchedulingItem(date = item.schedulingDate)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Navegação para histórico */ },
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Histórico de faltas aqui...", color = Color.DarkGray)
                }
            }
        }

        // --- Secção: Notas Próximo Agendamento ---
        ManagementSectionCard(
            title = "Notas Próximo Agendamento",
            icon = Icons.Default.Notifications,
            iconColor = Color(0xFF90CAF9),
            isExpanded = expandedSection == "pedidos",
            onClick = { expandedSection = if (expandedSection == "pedidos") null else "pedidos" }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Notas", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                SchedulingNotes(uiState.note)
            }
        }

        //--- Agendamentos Por Confirmar/Recusados ---
        if (uiState.declined?.isNotEmpty() == true){
            println(uiState.declined)
            ManagementSectionCard(
                title = "Recusados/Por Aceitar",
                icon = Icons.Default.Notifications,
                iconColor = Color(0xFF90CAF9),
                isExpanded = expandedSection == "recusados",
                onClick = { expandedSection = if (expandedSection == "recusados") null else "recusados" }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text("Recusados/Por Aceitar", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    uiState.declined.forEach { item ->
                        SchedulingManagementItem(
                            date = item.schedulingDate,
                            state = item.state
                        )
                    }

                }
            }
        }


        // --- BOTÃO NO FUNDO DA PÁGINA ---
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onAddNewScheduling,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Verde Social Store
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Novo Agendamento", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Espaço extra para não ficar colado à barra de navegação
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// --- Componentes Auxiliares ---

@Composable
fun ManagementSectionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    badgeText: String? = null,
    badgeColor: Color? = null,
    badgeTextColor: Color? = null,
    isExpanded: Boolean,
    onClick: () -> Unit,
    content: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
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
                if (badgeText != null) {
                    BadgeChip(badgeText, badgeColor, badgeTextColor)
                }
            }
            if (isExpanded && content != null) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

@Composable
fun BadgeChip(text: String, containerColor: Color?, contentColor: Color?) {
    Surface(
        color = containerColor ?: Color.LightGray,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor ?: Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SchedulingManagementItem(date: String, state : String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        if (state == "in_Progress"){
            Text(date, style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Default.Pending, null, tint = Color.Gray)
        }
        if (state == "decline"){
            Text(date, style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Default.Cancel, null, tint = Color.Gray)
        }
    }
}

@Composable
fun SchedulingItem(date: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(date, style = MaterialTheme.typography.bodyMedium)
        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
    }
}

@Composable
fun SchedulingNotes(note: String?) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = note ?: "Nenhuma nota para o próximo agendamento.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (note == null) Color.Gray else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManagement() {
    SocialStoreTheme {
        BeneficiaryManagementContent(
            modifier = Modifier,
            uiState = BeneficiaryManagementState(
                beneficiary = BeneficiaryModel(4, "Diogo", "1995-08-20", "",1),
                accept = 1,
                cancel = 0,
                note = "Entregar na porta lateral"
            ),
            onAddNewScheduling = {}
        )
    }
}