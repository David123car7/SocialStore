package com.ipca.socialstore.presentation.views.beneficiary.editProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic

@Composable
fun BeneficiaryEditProfileView(
    modifier: Modifier,
    navController: NavController,
    userRole: UserRole
){
    val viewModel: BeneficiaryEditProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    BeneficiaryEditProfileContent(
        modifier = modifier,
        uiState = uiState,
        onNameUpdate = viewModel::updateName,
        onBirthDateUpdate = viewModel::updateBirthDate,
        onPhoneNumberUpdate = viewModel::updatePhoneNumber,
        onUpdateProfile = {viewModel.updateBeneficiary()}
    )

    LaunchedEffect(uiState.isUpdated) {
        if(uiState.isUpdated){
            NavigationLogic.navigateTo(
                navController = navController,
                route = BeneficiaryRoutes.Profile,
                userRole = userRole
            )
        }
    }
}

@Composable
fun BeneficiaryEditProfileContent(
    modifier: Modifier,
    uiState: BeneficiaryEditProfileState,
    onNameUpdate:(String) -> Unit,
    onBirthDateUpdate:(String) -> Unit,
    onPhoneNumberUpdate:(String) -> Unit,
    onUpdateProfile:() -> Unit){

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
            text = "Editar Dados",
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
            value = uiState.beneficiary.name,
            icon = Icons.Default.Star,
            onValueUpdate = onNameUpdate
        )

        TextFieldDateComponent(
            modifier = Modifier,
            label = "Data de Nascimento",
            date = uiState.beneficiary.birthDate,
            onDatePickerUpdate = {},
            onDateUpdate = onBirthDateUpdate
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Telemovel",
            value = uiState.beneficiary.phoneNumber,
            icon = Icons.Default.Star,
            onValueUpdate = onPhoneNumberUpdate
        )

        Button(
            onClick = onUpdateProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Atualizar", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BenificiaryEditProfilePreview(){

}