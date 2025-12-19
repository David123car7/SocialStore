import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.socialstore.presentation.ui.components.DashboardCard
import com.ipca.socialstore.presentation.views.home.adminHome.DashboardMenuItem

// --- MODELOS DE DADOS ---

// Modelo para os botões do menu principa

// Modelo para o Log de Atividades
data class ActivityLog(
    val id: Int,
    val userName: String,
    val action: String,
    val time: String,
    val type: ActivityType
)

enum class ActivityType { SUBMISSION, CANCEL, STOCK, INFO }

// --- ECRÃ PRINCIPAL ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardMockup() {



    // 2. Dados Fictícios das Últimas Atividades
    val activities = listOf(
        ActivityLog(1, "António Silva", "submeteu uma nova candidatura", "Há 10 min", ActivityType.SUBMISSION),
        ActivityLog(2, "Luís Costa", "cancelou o agendamento de recolha", "Há 35 min", ActivityType.CANCEL),
        ActivityLog(3, "Sistema", "alertou para stock baixo de 'Arroz'", "Há 1 hora", ActivityType.STOCK),
        ActivityLog(4, "Maria Dias", "atualizou os dados do agregado", "Há 2 horas", ActivityType.INFO)
    )

    Scaffold(
        topBar = {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Olá, Admin", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = "Painel de Controlo", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Notifications, null)
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("A", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(8.dp))
                Text("Acesso Rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(16.dp))
                Text("Últimas Atividades", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(activities, span = { GridItemSpan(2) }) { activity ->
                ActivityLogCard(activity)
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
@Composable
fun ActivityLogCard(activity: ActivityLog) {
    val (icon, color) = when(activity.type) {
        ActivityType.SUBMISSION -> Icons.Default.Star to Color(0xFF1976D2)
        ActivityType.CANCEL -> Icons.Default.Star to Color(0xFFD32F2F)
        ActivityType.STOCK -> Icons.Default.Warning to Color(0xFFFFA000)
        ActivityType.INFO -> Icons.Default.Info to Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone Circular
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(activity.userName)
                        }
                        append(" ${activity.action}")
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(activity.time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    MaterialTheme {
        AdminDashboardMockup()
    }
}