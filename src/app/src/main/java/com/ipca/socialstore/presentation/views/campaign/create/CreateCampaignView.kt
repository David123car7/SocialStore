package com.ipca.socialstore.presentation.views.campaign.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
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
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.IconBgColor
import com.ipca.socialstore.presentation.ui.theme.IconTint
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic

@Composable
fun CreateCampaignView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val createCampaignViewModel: CreateCampaignViewModel = hiltViewModel()
    val uiState by createCampaignViewModel.uiState

    CreateCampaignContent(
        modifier = modifier,
        uiState = uiState,
        onNameUpdate = createCampaignViewModel::updateName,
        onCategoryUpdate = createCampaignViewModel::updateCategory,
        onDescriptionUpdate = createCampaignViewModel::updateDescription,
        onStartDateUpdate = createCampaignViewModel::updateStartDate,
        onEndDateUpdate = createCampaignViewModel::updateEndDate,
        onCreateCampaign = createCampaignViewModel::createCampaign
    )

    LaunchedEffect(uiState.isCreated) {
        if(uiState.isCreated){
            NavigationLogic.navigateTo(
                navController = navController,
                route = AdminRoutes.CampaignList,
                userRole = userRole
            )
        }
    }
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
    onCreateCampaign:() -> Unit
    ){
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Campaign, // Ou Icons.Default.Edit
            contentDescription = null,
            tint = IconTint,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = IconBgColor,
                    shape = CircleShape
                )
                .padding(12.dp)
        )

        Text(
            text = "Nova Campanha",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Preencha os dados para criar uma nova campanha.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Nome",
            value = uiState.campaign.name,
            icon = Icons.Outlined.Label,
            onValueUpdate = onNameUpdate
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Descrição",
            value = uiState.campaign.description,
            icon = Icons.Outlined.Description,
            onValueUpdate = onDescriptionUpdate
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Categoria",
            value = uiState.campaign.category,
            icon = Icons.Outlined.Category,
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
            label = "Data Fim",
            date = uiState.campaign.endDate,
            onDatePickerUpdate = {},
            onDateUpdate = onEndDateUpdate
        )

        Button(
            onClick = onCreateCampaign,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = buttonColors(GreenIPCA),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, name = "2. Formulário Preenchido")
@Composable
fun CreateCampaignPreview() {
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
                onCreateCampaign = {}
            )
        }

}