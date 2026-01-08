package com.ipca.socialstore.presentation.views.beneficiary.editProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.IconBgColor
import com.ipca.socialstore.presentation.ui.theme.IconTint
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.views.authentication.register.RegisterState
import com.ipca.socialstore.presentation.views.authentication.register.RegisterViewContent
import com.ipca.socialstore.presentation.views.mockups.Beneficiary

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
    modifier: Modifier = Modifier,
    uiState: BeneficiaryEditProfileState,
    onNameUpdate:(String) -> Unit,
    onBirthDateUpdate:(String) -> Unit,
    onPhoneNumberUpdate:(String) -> Unit,
    onUpdateProfile:() -> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = null,
            tint = IconTint,
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = IconBgColor,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
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
            icon = Icons.Outlined.Person,
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
            icon = Icons.Outlined.Phone,
            onValueUpdate = onPhoneNumberUpdate
        )

        Button(
            onClick = onUpdateProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = buttonColors(GreenIPCA)
        ) {
            Text("Atualizar", fontWeight = FontWeight.Bold)
        }

        // Espaço extra no fundo para garantir que o botão não fica colado ao fim do scroll
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BeneficiaryEditProfilePreview(){
    SocialStoreTheme() {
        val beneficiary = BeneficiaryModel(
            id = 1,
            name = "Nome",
            birthDate = "Data de Nascimento",
            phoneNumber = "Telemovel",
            academicId = null,
        )
        val uiState = BeneficiaryEditProfileState(beneficiary = beneficiary, isLoading = false, error = null, isUpdated = false)

        BeneficiaryEditProfileContent(
            modifier = Modifier,
            uiState = uiState,
            onNameUpdate = { Unit},
            onBirthDateUpdate = { Unit},
            onPhoneNumberUpdate = { Unit},
            onUpdateProfile = { Unit}
        )
    }
}