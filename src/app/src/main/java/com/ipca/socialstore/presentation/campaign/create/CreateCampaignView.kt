package com.ipca.socialstore.presentation.campaign.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable

fun CreateCampaingView(modifier: Modifier, navController: NavController){

    val viewModel : CreateCampaignViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    
    CreateCampaignViewContent(
        modifier = Modifier,
        uiState = uiState,
        onUpdateCampaignName = {value -> viewModel.updateCampaignName(value)},
        onUpdateDate = {value -> viewModel.updateDate(value)},
        createCampaign = {viewModel.createCampaign()}
    )
}
@Composable
fun CreateCampaignViewContent(
    modifier: Modifier,
    uiState : CampaignState,
    onUpdateCampaignName :(newValue: String)->Unit,
    onUpdateDate : (newValue : String) -> Unit,
    createCampaign : () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            value = uiState.campaign.name,
            label = { Text("Nome campanha") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onUpdateCampaignName(value)}
        )
        TextField(
            value = uiState.campaign.date,
            label = {Text("Data Campanha")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onUpdateDate(value)}
        )
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {createCampaign()}
        ) {
            Text("Criar Campanha")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateCampaignViewContent(){
    SocialStoreTheme() {
        val campaign = CampaignModel("","")
        val uiState = CampaignState(campaign,false,null,false)
        CreateCampaignViewContent(
            modifier = Modifier,
            uiState = uiState,
            onUpdateCampaignName = {Unit},
            onUpdateDate = { Unit},
            createCampaign = { Unit}
        )
    }
}