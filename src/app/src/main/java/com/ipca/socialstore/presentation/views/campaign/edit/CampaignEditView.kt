package com.ipca.socialstore.presentation.views.campaign.edit

import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.RequestType
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic

@Composable
fun CampaignEditView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val createCampaignViewModel: EditCampaignViewModel = hiltViewModel()
    val uiState by createCampaignViewModel.uiState

    CampaignEditContent(
        modifier = modifier,
        uiState = uiState,
        onNameUpdate = createCampaignViewModel::updateName,
        onCategoryUpdate = createCampaignViewModel::updateCategory,
        onDescriptionUpdate = createCampaignViewModel::updateDescription,
        onStartDateUpdate = createCampaignViewModel::updateStartDate,
        onEndDateUpdate = createCampaignViewModel::updateEndDate,
        onUpdateCampaign = createCampaignViewModel::editCampaign
    )

    LaunchedEffect(uiState.isEdited) {
        if(uiState.isEdited){
            NavigationLogic.navigateTo(
                navController = navController,
                route = AdminRoutes.CampaignList,
                userRole = userRole
            )
        }
    }
}

@Composable
fun CampaignEditContent(
    modifier: Modifier,
    uiState: EditCampaignState,
    onNameUpdate:(String) -> Unit,
    onDescriptionUpdate:(String) -> Unit,
    onCategoryUpdate:(String) -> Unit,
    onStartDateUpdate:(String) -> Unit,
    onEndDateUpdate:(String) -> Unit,
    onUpdateCampaign:() -> Unit
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Campaign, // Ou Icons.Default.Edit
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(12.dp)
        )

        Text(
            text = "Editar Campanha",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Preencha os dados que pretende editar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

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

        TextFieldDateComponent(
            modifier = Modifier,
            label = "Data Inicio",
            date = uiState.campaign.startDate,
            onDatePickerUpdate = {},
            onDateUpdate = onStartDateUpdate
        )

        TextFieldDateComponent(
            modifier = Modifier,
            label = "Data Inicio",
            date = uiState.campaign.endDate,
            onDatePickerUpdate = {},
            onDateUpdate = onEndDateUpdate
        )

        Button(
            onClick = onUpdateCampaign,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, name = "2. Formulário Preenchido")
@Composable
fun EditCampaignPreview() {
    val filledCampaign = CampaignModel(
        id = 1,
        name = "Recolha de Natal",
        description = "Angariação de brinquedos para crianças.",
        category = RequestType.FOOD.label,
        onGoing = true,
        startDate = "01/12/2024",
        endDate = "25/12/2024",
        goal = 100,
        currentDonations = 0
    )

    val filledState = EditCampaignState(
        campaign = filledCampaign
    )

    SocialStoreTheme {
        CampaignEditContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            uiState = filledState,
            onNameUpdate = {},
            onDescriptionUpdate = {},
            onCategoryUpdate = {},
            onEndDateUpdate = {},
            onStartDateUpdate = {},
            onUpdateCampaign = {}
        )
    }

}