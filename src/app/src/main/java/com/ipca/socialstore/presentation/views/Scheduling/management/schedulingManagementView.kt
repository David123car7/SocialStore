package com.ipca.socialstore.presentation.views.Scheduling.management

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.components.SearchBarContent

@Composable
fun SchedulingManagementView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : SchedulingManagementViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    SchedulingManagementContent(
        modifier = modifier,
        uiState = uiState,
        onSearchBeneficiary = {value -> viewModel.onSearchBeneficiary(value)},
        onClick = { item ->
            val routeName = AdminRoutes.BeneficiaryManagement::class.qualifiedName!!
            navController.navigate("$routeName/${item.id}")
        }
    )
}
@Composable
fun SchedulingManagementContent(
    modifier: Modifier,
    uiState : SchedulingManagementState,
    onSearchBeneficiary : (value : String) -> Unit,
    onClick:  (BeneficiaryModel) -> Unit,
){
    Column(
        modifier.fillMaxSize()
    ) {
        SearchBarContent({value -> onSearchBeneficiary(value)})

        LazyColumn(
            modifier.fillMaxSize()
        ) {
            itemsIndexed(uiState.filteredBeneficiaries ?: emptyList()){index, item ->
                BeneficiaryCard(
                    item.name,
                    item.id.toString(),
                    status = "Regular",
                    onClick = {onClick(item)}
                )
            }
        }
    }
}


@Composable
fun BeneficiaryCard(
    name: String,
    processNumber: String,
    status: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Processo nº $processNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

            }
            Surface(
                color = if (status == "Falta") Color(0xFFFFDAD4) else Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (status == "Falta") Color.Red else Color(0xFF136342),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SchedulingManagementPreview() {
    val mockBeneficiaries = listOf(
        BeneficiaryModel(
            id = 4,
            name = "Diogo",
            birthDate = "1995-08-20",
            academicId = 2,
            phoneNumber = ""
        ),
        BeneficiaryModel(
            id = 3,
            name = "David",
            birthDate = "1990-05-15",
            academicId = 1,
            phoneNumber = ""
        )
    )

    // 2. Simular o estado da UI
    val mockUiState = SchedulingManagementState(
        beneficiaries = mockBeneficiaries,
        filteredBeneficiaries = mockBeneficiaries, // Mostra todos inicialmente
        isLoading = false
    )

    MaterialTheme {
        // 3. Chamar o conteúdo da View
        SchedulingManagementContent(
            modifier = Modifier.padding(16.dp),
            uiState = mockUiState,
            onSearchBeneficiary = {},
            onClick = {}
        )
    }
}