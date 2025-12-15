package com.ipca.socialstore.presentation.views.mockups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- ECRÃ HOME (NÃO BENEFICIÁRIO) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NonBeneficiaryHomeScreenMockup(
    onStartApplicationClick: () -> Unit = {},
    onSeeMoreClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Social Store", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Menu Perfil */ }) {
                        Icon(Icons.Default.Person, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // 1. HERO SECTION (Chamada para Ação Principal)
            HeroApplicationBanner(onClick = onStartApplicationClick)

            // 2. COMO FUNCIONA (Passos)
            Text("Como funciona?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            HowItWorksSection()

            // 3. SERVIÇOS DISPONÍVEIS (O que podem ganhar)
            Text("Apoios Disponíveis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            AvailableServicesRow()

            // 4. CRITÉRIOS / DÚVIDAS
            InfoCard()

            // Espaço fundo
            Spacer(Modifier.height(32.dp))
        }
    }
}

// --- COMPONENTES ---

@Composable
fun HeroApplicationBanner(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1976D2), Color(0xFF1565C0))
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Precisa de Apoio?",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "A Loja Social está aqui para ajudar a comunidade académica. Submeta a sua candidatura para aceder a bens alimentares, material escolar e mais.",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Candidatar-me Agora", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun HowItWorksSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StepItem("1", "Preencher\nDados", Icons.Default.Star)
        StepDivider()
        StepItem("2", "Análise\nTécnica", Icons.Default.Star)
        StepDivider()
        StepItem("3", "Receber\nApoio", Icons.Default.Star)
    }
}

@Composable
fun RowScope.StepDivider() {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .align(Alignment.CenterVertically)
            .background(Color.LightGray)
    )
}

@Composable
fun StepItem(number: String, text: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(70.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun AvailableServicesRow() {
    val services = listOf(
        "Alimentação" to Icons.Default.Star,
        "Vestuário" to Icons.Default.Star,
        "Escolar" to Icons.Default.Star,
        "Higiene" to Icons.Default.Star,
        "Mobiliário" to Icons.Default.Star
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(services) { (name, icon) ->
            ServiceCard(name, icon)
        }
    }
}

@Composable
fun ServiceCard(name: String, icon: ImageVector) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = Modifier.size(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Quem pode candidatar-se?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Estudantes, docentes e funcionários do IPCA que se encontrem em situação de vulnerabilidade económica comprovada.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NonBeneficiaryHomePreview() {
    MaterialTheme {
        NonBeneficiaryHomeScreenMockup()
    }
}