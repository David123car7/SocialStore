
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- MODELOS ---
data class ApplicationSummary(
    val id: Int,
    val name: String,
    val course: String,
    val date: String,
    val status: ApplicationStatus
)

enum class ApplicationStatus { PENDING, APPROVED, REJECTED }

// --- ECRÃ ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllApplicationsScreenMockup() {
    // Dados fictícios
    val applications = listOf(
        ApplicationSummary(1, "Maria Gomes", "Eng. Informática", "Há 2 horas", ApplicationStatus.PENDING),
        ApplicationSummary(2, "João Silva", "Gestão", "Ontem", ApplicationStatus.APPROVED),
        ApplicationSummary(3, "Ana Sousa", "Design", "12 Out", ApplicationStatus.REJECTED),
        ApplicationSummary(4, "Pedro Santos", "Medicina", "10 Out", ApplicationStatus.PENDING)
    )

    // Estado do filtro selecionado
    var selectedFilter by remember { mutableStateOf(ApplicationStatus.PENDING) }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(title = { Text("Candidaturas") })
                // Barra de Pesquisa Simulada
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Pesquisar nome ou nº...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // 1. Filtros (Chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == ApplicationStatus.PENDING,
                    onClick = { selectedFilter = ApplicationStatus.PENDING },
                    label = { Text("Pendentes") },
                    leadingIcon = if (selectedFilter == ApplicationStatus.PENDING) {
                        { Icon(Icons.Default.Check, null) }
                    } else null
                )
                FilterChip(
                    selected = selectedFilter == ApplicationStatus.APPROVED,
                    onClick = { selectedFilter = ApplicationStatus.APPROVED },
                    label = { Text("Histórico") }
                )
            }

            // 2. A Lista
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(applications) { app ->
                    ApplicationListCard(app)
                }
            }
        }
    }
}

@Composable
fun ApplicationListCard(app: ApplicationSummary) {
    // Definir cores baseadas no estado
    val (statusColor, statusText) = when (app.status) {
        ApplicationStatus.PENDING -> Color(0xFFFBC02D) to "Em Análise" // Amarelo
        ApplicationStatus.APPROVED -> Color(0xFF388E3C) to "Aprovado"   // Verde
        ApplicationStatus.REJECTED -> Color(0xFFD32F2F) to "Rejeitado"  // Vermelho
    }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().clickable { /* Abrir Detalhes */ }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar / Iniciais
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = app.name.take(1), // Primeira letra
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Informação Principal
            Column(modifier = Modifier.weight(1f)) {
                Text(app.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(app.course, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(Modifier.height(4.dp))

                // Data pequena
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(app.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            // Badge de Estado
            Surface(
                color = statusColor.copy(alpha = 0.1f), // Fundo suave
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AllApplicationsScreenPreview() {
    MaterialTheme {
        AllApplicationsScreenMockup()
    }
}