package com.ipca.socialstore.presentation.views.Scheduling

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun CreateSchedulingView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : CreateSchedulingView = hiltViewModel()
    val uiState by viewModel.uiState

    CreateSchedulingViewContent(
        modifier = modifier,
        uiState = uiState,
        onUpdateSubject = {newValue -> viewModel.updateSubject(newValue)},
        onUpdateDate = {newValue -> viewModel.updateDate(newValue)},
        onUpdateBeneficiary = {newValue -> viewModel.updateBeneficiaryId(newValue)},
        onUpdateCreateAt = {viewModel.updateCreateAt()},
        onCreate = {viewModel.createScheduling()}
    )
}

@Composable
fun CreateSchedulingViewContent(
    modifier : Modifier,
    uiState : CreateSchedulingState,
    onUpdateSubject : (newValue : String) -> Unit,
    onUpdateDate : (newValue : String) -> Unit,
    onUpdateBeneficiary : (newValue : String) -> Unit,
    onUpdateCreateAt : () -> Unit,
    onCreate : () -> Unit,
){
    Column(
        modifier.fillMaxSize()
    ) {
        TextField(
            value = uiState.notification.subject,
            label = {Text("Mensagem")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onUpdateSubject(value) }
        )
        TextField(
            value = uiState.schedulingDate?.date ?: "",
            label = {Text("Data de agendamento")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onUpdateDate(value) }
        )
        TextField(
            value = uiState.beneficiaryId ?: "",
            label = {Text("Id")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onUpdateBeneficiary(value) }
        )
        Button(
            onClick ={
                onUpdateCreateAt()
                onCreate()
            }
        ) {
            Text("Create Scheduling")
        }
    }
}

@Preview (showBackground = true)
@Composable
fun Preview(){
    SocialStoreTheme() {
        val uiState = CreateSchedulingState(null,null,null,false,null)
        CreateSchedulingViewContent(
            modifier = Modifier,
            uiState = uiState,
            onUpdateDate = {},
            onUpdateSubject = {},
            onUpdateBeneficiary = {},
            onCreate = {},
            onUpdateCreateAt = {}
        )
    }
}