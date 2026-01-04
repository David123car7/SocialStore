package com.ipca.socialstore.presentation.views.home.beneficiaryHome

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun BeneficiaryHomeView(modifier: Modifier, userRole : UserRole){
    BenificiaryHomeContent(modifier = modifier)
}

data class MenuAction(
    val title: String,
    val icon: ImageVector
)
@Composable
fun BenificiaryHomeContent(modifier: Modifier){
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Cabeçalho
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Olá, Tiago!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Bem-vindo de volta", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                //Icon( Icons.Outlined.Notifications, contentDescription = null, modifier = Modifier.size(32.dp))
            }

            // 2. Estado da Candidatura (Destaque)
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)) // Laranja suave (Em análise)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Candidatura 2024", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Badge(containerColor = Color(0xFFF57C00)) { Text("Em Análise", color = Color.White, modifier = Modifier.padding(4.dp)) }
                    }
                    Column {
                        Text("Passo 3 de 4 concluídos", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(progress = 0.75f, modifier = Modifier.fillMaxWidth(), color = Color(0xFFF57C00))
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00))
                    ) {
                        Text("Ver Detalhes")
                    }
                }
            }

            // 3. Ações Rápidas (Grid)
            Text("Acesso Rápido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            val actions = listOf(
                MenuAction("Meus Documentos", Icons.Default.Add),
                MenuAction("Dados Pessoais", Icons.Default.Person),
                MenuAction("Mensagens", Icons.Default.MailOutline),
                MenuAction("Histórico", Icons.Default.Home) // Mudei History para DateRange para garantir que funciona
            )

// 2. A Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth() // Importante dar tamanho
            ) {
                // Usa 'items' importado de: androidx.compose.foundation.lazy.grid.items
                items(actions) { item ->

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        modifier = Modifier
                            .height(100.dp) // Dá uma altura fixa ou mínima aos cartões
                            .clickable {
                                // Ação de clique aqui
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Agora mostramos o Ícone e o Texto
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = Color.Gray
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = item.title, // Já não precisas de .first, usa o nome real
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BenificiaryHomePreview(){
    SocialStoreTheme() {
        BenificiaryHomeContent(modifier = Modifier)
    }
}

