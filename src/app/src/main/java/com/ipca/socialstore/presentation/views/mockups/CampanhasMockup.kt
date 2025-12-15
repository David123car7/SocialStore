import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- MODELO DE DADOS RENOMEADO ---
data class CampaignTest(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // Alimentar, Escolar, Roupa
    val currentAmount: Int,
    val targetAmount: Int,
    val daysLeft: Int,
    val color: Color
)

// --- ECRÃ DE CAMPANHAS ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsScreenMockup(
    onCampaignClick: (String) -> Unit = {}
) {
    // Categorias para filtro
    val categories = listOf("Todas", "Alimentar", "Material Escolar", "Roupa", "Dinheiro")
    var selectedCategory by remember { mutableStateOf("Todas") }

    // Dados Fictícios usando CampaignTest
    val campaigns = listOf(
        CampaignTest("1", "Recolha de Natal 2025", "Ajude-nos a compor 500 cabazes para as famílias mais carenciadas da comunidade académica.", "Alimentar", 350, 500, 12, Color(0xFFC62828)),
        CampaignTest("2", "Kit Escolar Solidário", "Recolha de cadernos, canetas e calculadoras para o segundo semestre.", "Material Escolar", 120, 200, 45, Color(0xFF1976D2)),
        CampaignTest("3", "Roupa Quente", "Recolha de casacos e mantas para o Inverno.", "Roupa", 45, 100, 5, Color(0xFFF57C00))
    )

    // Filtragem
    val filteredCampaigns = if (selectedCategory == "Todas") {
        campaigns
    } else {
        campaigns.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Campanhas Ativas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // 1. FILTROS (Chips Horizontais)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        leadingIcon = if (selectedCategory == category) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            // 2. LISTA DE CAMPANHAS
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header motivacional
                item {
                    Text(
                        "Juntos fazemos a diferença.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(filteredCampaigns) { campaign ->
                    CampaignCard(campaign, onClick = { onCampaignClick(campaign.id) })
                }
            }
        }
    }
}

// --- COMPONENTE DO CARTÃO DE CAMPANHA ---
@Composable
fun CampaignCard(campaign: CampaignTest, onClick: () -> Unit) {
    val progress = campaign.currentAmount.toFloat() / campaign.targetAmount.toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // 1. IMAGEM DE CAPA (Simulada com Box)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(campaign.color, campaign.color.copy(alpha = 0.6f))
                        )
                    )
            ) {
                // Badge de Categoria
                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart)
                ) {
                    Text(
                        text = campaign.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = campaign.color
                    )
                }

                // Ícone decorativo
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(100.dp).align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp)
                )
            }

            // 2. CONTEÚDO
            Column(modifier = Modifier.padding(16.dp)) {

                // Título e Dias Restantes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = campaign.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Chip de Tempo
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Star, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${campaign.daysLeft} dias",
                            style = MaterialTheme.typography.bodySmall,
                            color = if(campaign.daysLeft < 7) Color.Red else Color.Gray,
                            fontWeight = if(campaign.daysLeft < 7) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = campaign.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(20.dp))

                // 3. BARRA DE PROGRESSO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}% angariado",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = campaign.color
                    )
                    Text(
                        text = "${campaign.currentAmount} / ${campaign.targetAmount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = campaign.color,
                    trackColor = campaign.color.copy(alpha = 0.1f),
                )

                Spacer(Modifier.height(16.dp))

                // Botão de Ação
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Contribuir Agora")
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CampaignsPreview() {
    MaterialTheme {
        CampaignsScreenMockup()
    }
}