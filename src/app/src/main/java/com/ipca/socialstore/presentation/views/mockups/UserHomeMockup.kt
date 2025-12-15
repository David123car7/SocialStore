package com.ipca.socialstore.presentation.views.mockups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

// --- ENUM DE ESTADOS ATUALIZADO ---
enum class UserStatus {
    PENDING,            // Em análise (Tracker + FAQs)
    NEEDS_CORRECTION,   // Ação necessária (Aviso Amarelo/Laranja)
    REJECTED,           // Recusado (Explicação + Agendar Reunião)
    ACTIVE              // Aceite (Cartão Digital)
}

// --- ECRÃ PRINCIPAL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreenMockup() {

    // TROCA AQUI O ESTADO PARA TESTARES OS VISUAIS:
    // UserStatus.PENDING, UserStatus.NEEDS_CORRECTION, UserStatus.REJECTED, UserStatus.ACTIVE
    val userStatus by remember { mutableStateOf(UserStatus.ACTIVE)}
    val userName = "Maria Silva"

    Scaffold(
        topBar = {
            // TopBar mais limpa
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Social Store", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = when(userStatus) {
                                UserStatus.ACTIVE -> "Bem-vindo(a)"
                                UserStatus.PENDING -> "Processo a decorrer"
                                UserStatus.NEEDS_CORRECTION -> "Ação Necessária"
                                UserStatus.REJECTED -> "Estado do Pedido"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        BadgedBox(badge = { Badge { Text("1") } }) {
                            Icon(Icons.Outlined.Notifications, null)
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 1. ÁREA HERO (O Cartão Principal)
            item {
                Spacer(Modifier.height(8.dp))
                when (userStatus) {
                    UserStatus.ACTIVE -> ActiveCard(userName)
                    UserStatus.PENDING -> PendingTrackerCard()
                    UserStatus.NEEDS_CORRECTION -> CorrectionCard()
                    UserStatus.REJECTED -> RejectedCard()
                }
            }

            // 2. CONTEÚDO DE APOIO (Para não ficar vazio)
            item {
                when (userStatus) {
                    // Se estiver PENDENTE: Mostra FAQs para acalmar ansiedade
                    UserStatus.PENDING -> PendingContentSection()

                    // Se precisar CORREÇÃO: Mostra botão grande e instruções
                    UserStatus.NEEDS_CORRECTION -> CorrectionContentSection()

                    // Se estiver REJEITADO: Mostra opções de recurso/apoio
                    UserStatus.REJECTED -> RejectedContentSection()

                    // Se estiver ATIVO: Menu normal
                    UserStatus.ACTIVE -> ActiveMenuSection()
                }
            }

            // Espaço final
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// ==========================================
// SECÇÕES DE CONTEÚDO (O QUE VAI POR BAIXO)
// ==========================================

@Composable
fun PendingContentSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Perguntas Frequentes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        InfoCard(
            icon = Icons.Outlined.Star,
            title = "Quanto tempo demora?",
            desc = "A análise demora, em média, 3 a 5 dias úteis. Receberá uma notificação assim que terminarmos."
        )
        InfoCard(
            icon = Icons.Outlined.Phone,
            title = "Precisa de urgência?",
            desc = "Se a sua situação é crítica, contacte a linha de apoio social: 253 000 000."
        )

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.Black)
        ) {
            Icon(Icons.Default.Star, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Ver a minha candidatura submetida")
        }
    }
}

@Composable
fun CorrectionContentSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("O que precisa de fazer:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Item específico do erro (Simulado)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            border = BorderStroke(1.dp, Color(0xFFFFB74D))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFF57C00))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("IRS Incompleto", fontWeight = FontWeight.Bold)
                    Text("O documento enviado não está legível. Por favor digitalize novamente.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }

        Button(
            onClick = { /* Navegar para editar */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)) // Laranja
        ) {
            Text("Resolver Problema Agora")
        }
    }
}

@Composable
fun RejectedContentSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Outras Opções", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        InfoCard(
            icon = Icons.Default.Star,
            title = "Agendar Reunião",
            desc = "Gostaria de rever o seu processo com um assistente social? Marque um atendimento presencial."
        )

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Detalhes da Decisão")
        }
    }
}

@Composable
fun ActiveMenuSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Acesso Rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuButton("Novo Pedido", Icons.Default.Star, Color(0xFFE3F2FD), Modifier.weight(1f))
            MenuButton("Histórico", Icons.Default.Star, Color(0xFFF3E5F5), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuButton("Agenda", Icons.Default.Star, Color(0xFFE8F5E9), Modifier.weight(1f))
            MenuButton("Perfil", Icons.Default.Person, Color(0xFFFFF3E0), Modifier.weight(1f))
        }
    }
}

// ==========================================
// CARDS DE TOPO (HERO)
// ==========================================

@Composable
fun CorrectionCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // Laranja claro
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, null, tint = Color(0xFFE65100), modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Text("Atenção Necessária", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }
            Spacer(Modifier.height(12.dp))
            Text("Detetámos um problema com a sua candidatura que impede a aprovação. Por favor, verifique os detalhes abaixo.",
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun RejectedCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), // Vermelho claro
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFC62828), modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Text("Não Aceite", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
            }
            Spacer(Modifier.height(12.dp))
            Text("Lamentamos, mas a sua candidatura não cumpre os requisitos atuais para apoio. Pode consultar os motivos detalhados ou agendar um esclarecimento.",
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PendingTrackerCard() {
    // Igual ao anterior mas mais compacto se quiseres
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), shape = RoundedCornerShape(24.dp)) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Estado da Candidatura", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(progress = { 0.5f }, modifier = Modifier.size(48.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Em Análise Técnica", fontWeight = FontWeight.Bold)
                    Text("Atualizado há 2 dias", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ActiveCard(name: String) {
    // O teu cartão bonito
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth().height(180.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(Color(0xFF1976D2), Color(0xFF42A5F5)))
            )
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Cartão Digital", color = Color.White.copy(0.8f))
                    Icon(Icons.Default.Star, null, tint = Color.White)
                }
                Text(name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// COMPONENTES GENÉRICOS
// ==========================================

@Composable
fun InfoCard(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun MenuButton(title: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp).clickable { },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, null, tint = Color.Black.copy(0.6f))
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    UserHomeScreenMockup()
}