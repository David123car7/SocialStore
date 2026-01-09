package com.ipca.socialstore.presentation.views.reports

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipca.socialstore.presentation.models.ReportsHelperModel
import com.ipca.socialstore.presentation.ui.components.IntroductionComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA // A tua cor personalizada

@Composable
fun ReportsView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel: ReportsViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    ReportsContent(
        modifier = modifier,
        uiState = uiState,
        onExportReport = {}
    )
}

@Composable
fun ReportsContent(
    modifier: Modifier,
    uiState: ReportsState,
    onExportReport:(String)-> Unit){
    var selectedCategory by remember { mutableStateOf("Todos") }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Cabeçalho Geral
        IntroductionComponent(
            tittle = "Relatórios de Stock",
            description = "Consulte o balanço de entradas e saídas.",
            icon = Icons.Filled.Assessment
        )

        // 2. Filtros (Chips de Categoria)
        CategoryFilterSection(
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Button(
            onClick = { onExportReport(selectedCategory) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA),
            elevation = ButtonDefaults.buttonElevation(2.dp)
        ) {
            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Exportar Relatório (.csv)", color = Color.White)
        }

        // 3. Tabela de Dados
        // Usamos um Card para dar uma moldura bonita à tabela
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Ocupa o resto do ecrã
        ) {
            Column {
                ReportTableHeader()

                HorizontalDivider(color = Color.LightGray)

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(uiState.reports) { index, item ->
                        ReportTableRow(
                            item = item,
                            isEven = index % 2 == 0 // Para efeito zebrado (opcional)
                        )
                        if (index < uiState.reports.lastIndex) {
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Todos", "Alimentar", "Higiene", "Limpeza")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GreenIPCA.copy(alpha = 0.2f),
                    selectedLabelColor = GreenIPCA.copy(alpha = 1f), // Texto sólido
                    selectedLeadingIconColor = GreenIPCA
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedCategory == category,
                    borderColor = if (selectedCategory == category) GreenIPCA else Color.Gray.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun ReportTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)) // Fundo cinza claro para o cabeçalho
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Item",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = "Total",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            text = "Entr.", // Abreviado para caber bem em ecrãs pequenos
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )
        Text(
            text = "Stock",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
fun ReportTableRow(item: ReportsHelperModel, isEven: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Efeito Zebrado muito subtil (opcional)
            .background(if (isEven) Color.Transparent else Color.Black.copy(alpha = 0.02f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Nome do Item
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis, // Corta com "..." se for muito grande
            modifier = Modifier.weight(0.4f)
        )

        // 2. Total
        Text(
            text = item.total.toString(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )

        // 3. Entregue
        Text(
            text = item.delivered.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )

        val stockColor = if (item.stock == 0) MaterialTheme.colorScheme.error else GreenIPCA
        val stockWeight = if (item.stock == 0) FontWeight.Bold else FontWeight.Normal

        Text(
            text = item.stock.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = stockColor,
            fontWeight = stockWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsPreview(){

}