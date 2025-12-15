package com.ipca.socialstore.presentation.views.mockups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Importante para o items(list)
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.* // Importa Material 3 (Scaffold, Text, Icon, etc.)
import androidx.compose.runtime.* // Importa remember, mutableStateOf, etc.
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- MODELO ---
data class Beneficiary(
    val id: Int,
    val name: String,
    val email: String,
    val householdSize: Int,
    val isActive: Boolean
)

// --- ECRÃ ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiariesListScreenMockup() {
    // Dados fictícios
    val allBeneficiaries = remember { listOf(
        Beneficiary(1, "Ana Pereira", "ana.p@email.com", 3, true),
        Beneficiary(2, "Carlos Manuel", "carlos.m@email.com", 1, true),
        Beneficiary(3, "Beatriz Costa", "bia.costa@email.com", 5, false),
        Beneficiary(4, "Diogo Fernandes", "diogo.f@email.com", 2, true)
    )}

    // Estado da Pesquisa
    var searchQuery by remember { mutableStateOf("") }

    // Lógica de Filtro
    val filteredList = if (searchQuery.isBlank()) {
        allBeneficiaries
    } else {
        allBeneficiaries.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            Column(Modifier.padding(bottom = 8.dp)) {
                CenterAlignedTopAppBar(
                    title = { Text("Beneficiários") },
                    actions = {
                        IconButton(onClick = {}) { Icon(Icons.Default.Search, null) }
                    }
                )

                // Barra de Pesquisa
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Procurar nome...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Adicionar Novo */ }) {
                Icon(Icons.Default.Person, null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Contador
            item {
                Text(
                    "${filteredList.size} registos encontrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(filteredList) { beneficiary ->
                BeneficiaryListItem(beneficiary)
            }
        }
    }
}

@Composable
fun BeneficiaryListItem(user: Beneficiary) {
    ListItem(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { /* Abrir Perfil */ }
            .background(Color(0xFFF5F5F5)),
        headlineContent = {
            Text(user.name, fontWeight = FontWeight.Bold)
        },
        supportingContent = {
            Column {
                Text(user.email)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text("Agregado: ${user.householdSize}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        },
        leadingContent = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = user.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        trailingContent = {
            val (color, text) = if (user.isActive) {
                Color(0xFF2E7D32) to "Ativo"
            } else {
                Color.Gray to "Inativo"
            }

            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = text,
                    color = color,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BeneficiariesListScreenPreview() {
    // We wrap it in MaterialTheme to get the correct default fonts and colors
    MaterialTheme {
        BeneficiariesListScreenMockup()
    }
}