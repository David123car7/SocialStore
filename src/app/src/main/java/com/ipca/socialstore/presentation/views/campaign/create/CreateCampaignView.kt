package com.ipca.socialstore.presentation.views.campaign.create

import android.view.Surface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.RequestType
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.presentation.ui.components.SocialStoreDropdown
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.views.campaign.list.CampaignsListViewModel

@Composable
fun CreateCampaignView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val createCampaignViewModel: CreateCampaignViewModel = hiltViewModel()
    val uiState by createCampaignViewModel.uiState

    CreateCampaignContent(
        modifier = modifier,
        uiState = uiState,
        onNameUpdate = {},
        onCategoryUpdate = {},
        onDescriptionUpdate = {},
        onStartDateUpdate = {},
        onEndDateUpdate = {},
        onGoingUpdate = {}
    )
}

@Composable
fun CreateCampaignContent(
    modifier: Modifier,
    uiState: CreateCampaignState,
    onNameUpdate:(String) -> Unit,
    onDescriptionUpdate:(String) -> Unit,
    onCategoryUpdate:(String) -> Unit,
    onStartDateUpdate:(String) -> Unit,
    onEndDateUpdate:(String) -> Unit,
    onGoingUpdate:(String) -> Unit,
    ){
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldStringComponent(
            modifier = Modifier,
            label = "Nome",
            value = uiState.campaign.name,
            icon = Icons.Default.Star,
            onValueUpdate = onNameUpdate
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Descrição",
            value = uiState.campaign.description,
            icon = Icons.Default.Star,
            onValueUpdate = onDescriptionUpdate
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Categoria",
            value = uiState.campaign.category,
            icon = Icons.Default.Star,
            onValueUpdate = onCategoryUpdate
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Data Inicio",
            value = uiState.campaign.startDate,
            icon = Icons.Default.Star,
            onValueUpdate = onStartDateUpdate
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Data Fim",
            value = uiState.campaign.endDate,
            icon = Icons.Default.Star,
            onValueUpdate = onEndDateUpdate
        )
    }
}

@Preview(showBackground = true, name = "2. Formulário Preenchido")
@Composable
fun CreateCampaignPreview() {
    val filledCampaign = CampaignModel(
        id = 1,
        name = "Recolha de Natal",
        description = "Angariação de brinquedos para crianças.",
        category = RequestType.FOOD.label, // Substitui por um valor real do teu Enum (ex: "Alimentar")
        onGoing = true,
        startDate = "01/12/2024",
        endDate = "25/12/2024"
    )

    val filledState = CreateCampaignState(
        campaign = filledCampaign
    )

    SocialStoreTheme {
            CreateCampaignContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                uiState = filledState,
                onNameUpdate = {},
                onDescriptionUpdate = {},
                onCategoryUpdate = {},
                onEndDateUpdate = {},
                onStartDateUpdate = {},
                onGoingUpdate = {}
            )
        }

}