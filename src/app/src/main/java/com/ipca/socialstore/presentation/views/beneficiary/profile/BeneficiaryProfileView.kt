package com.ipca.socialstore.presentation.views.beneficiary.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.ui.components.CategoryBoxEdit
import com.ipca.socialstore.presentation.ui.components.IntroductionComponent
import com.ipca.socialstore.presentation.ui.components.ReadOnlyField
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox
import com.ipca.socialstore.presentation.views.beneficiary.listDocuments.ListDocumentsViewModel

@Composable
fun BeneficiaryProfileView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel: BeneficiaryProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    BeneficiaryProfileContent(
        modifier = modifier,
        uiState = uiState,
        onEditClick = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = BeneficiaryRoutes.EditProfile
            )
        }
    )
}

@Composable
fun BeneficiaryProfileContent(
    modifier: Modifier,
    uiState: BeneficiaryProfileState,
    onEditClick:() -> Unit
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        IntroductionComponent(
            tittle = "Os meus Dados",
        )

        CategoryBoxEdit(
            title = "Dados Pessoais",
            onEditClick = onEditClick
        ) {
            ReadOnlyField("Nome Completo", uiState.beneficiary.name)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    ReadOnlyField("Data Nasc.", uiState.beneficiary.birthDate)
                }
                Box(Modifier.weight(1f)) {
                    ReadOnlyField("Telemóvel", uiState.beneficiary.phoneNumber)
                }
            }
        }

        if(uiState.academicData.id != null){
            CategoryBox(
                title = "Dados Académicos",
            ) {
                ReadOnlyField("Numero Estudante: ", uiState.academicData.studenNumber)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.weight(1f)) {
                        ReadOnlyField("Tipo de Curso: ", uiState.academicData.typeCourse)
                    }
                    Box(Modifier.weight(1f)) {
                        ReadOnlyField("Curso: ", uiState.academicData.course)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BeneficiaryProfilePreview(){
    val uiState = BeneficiaryProfileState(
        beneficiary = createEmptyBeneficiary(),
        error = null
    )

    BeneficiaryProfileContent(
        modifier = Modifier,
        uiState = uiState,
        onEditClick = {}
    )
}