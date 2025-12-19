package com.ipca.socialstore.presentation.views.application.status

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.ui.SocialStoreScaffold
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox
import com.ipca.socialstore.presentation.views.application.applicationState.DocumentRow
import com.ipca.socialstore.presentation.views.application.applicationState.ReadOnlyField

// --- ENUM DE ESTADOS ---
enum class ApplicationState(
    val label: String,
    val description: String,
    val color: Color,
    val step: Int
) {
    REVIEW("Em Análise", "A sua candidatura está a ser analisada.", Color(0xFFFBC02D), 2),
    ACCEPTED("Aceite", "Candidatura aprovada.", Color(0xFF43A047), 3),
    DENIED("Recusado", "Candidatura não aceite.", Color(0xFFD32F2F), 3),
    CORRECTION("Correção", "Necessária atenção.", Color(0xFF1976D2), 2)
}

// --- VIEW PRINCIPAL ---
@Composable
fun ApplicationStatusView(
    navController: NavController,
    userRole: UserRole,
    currentState: ApplicationState,
    onDeleteApplication: () -> Unit
) {
    SocialStoreScaffold(navController = navController, userRole = userRole) { innerPadding ->
        ApplicationStatusViewContent(
            modifier = Modifier.padding(innerPadding),
            currentState = currentState,
            onDeleteApplication = onDeleteApplication
        )
    }
}

// --- CONTEÚDO VISUAL ---
@Composable
fun ApplicationStatusViewContent(
    modifier: Modifier = Modifier,
    currentState: ApplicationState,
    onDeleteApplication: () -> Unit
) {
    var isDataExpanded by remember { mutableStateOf(false) }
    var isDocsExpanded by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA)) // Fundo cinza suave
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text("Minha Candidatura", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        ExpandableSection(
            title = "Meus Dados",
            icon = Icons.Outlined.Person,
            isExpanded = isDataExpanded,
            onExpandChange = { isDataExpanded = it }
        ) {
            CategoryBox(title = "Dados Pessoais", bgColor = Color.White) {
                ReadOnlyField("Nome Completo", "João Pedro Silva Santos")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { ReadOnlyField("NIF", "234567890") }
                    Box(Modifier.weight(1f)) { ReadOnlyField("Data Nasc.", "15/03/2000") }
                }
                ReadOnlyField("Morada", "Rua das Flores, nº 123, 4º Esq., 1000-001 Lisboa")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { ReadOnlyField("Telemóvel", "+351 912 345 678") }
                    Box(Modifier.weight(1f)) { ReadOnlyField("Agregado", "3 Pessoas") }
                }
            }

            Spacer(Modifier.height(16.dp))

            CategoryBox(title = "Dados Académicos", bgColor = Color.White) {
                ReadOnlyField("Instituição", "IPCA - Barcelos")
                ReadOnlyField("Curso", "Engenharia em Desenvolvimento de Jogos Digitais")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) { ReadOnlyField("Nº Estudante", "a12345") }
                    Box(Modifier.weight(1f)) { ReadOnlyField("Ano Curricular", "3º Ano") }
                }
            }
        }

        // Botão Eliminar
        if (currentState != ApplicationState.DENIED && currentState != ApplicationState.ACCEPTED) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onDeleteApplication,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Anular Candidatura")
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun DocumentRow(x0: String, x1: String, x2: String, x3: Color, x4: Color) {
    TODO("Not yet implemented")
}

// --- COMPONENTE NOVO: CAIXA DE CATEGORIA ---
// Isto cria as caixas brancas com borda cinza para cada grupo

// --- COMPONENTE: LINHA DE DOCUMENTO ---


// --- OUTROS COMPONENTES (Timeline, ReadOnlyField, ExpandableSection) ---
// Mantidos para garantir que o código funciona completo



@Composable
fun RowScope.TimelineLine(isActive: Boolean, color: Color) {
    Box(
        modifier = Modifier.weight(1f).height(3.dp).padding(horizontal = 4.dp)
            .background(if (isActive) color else Color(0xFFE0E0E0))
            .align(Alignment.CenterVertically).offset(y = (-10).dp)
    )
}

// --- PREVIEW ---
@Preview(showBackground = true, heightDp = 1000)
@Composable
fun ApplicationStatusBoxesPreview() {
    SocialStoreTheme {
        ApplicationStatusViewContent(
            currentState = ApplicationState.ACCEPTED,
            onDeleteApplication = {}
        )
    }
}