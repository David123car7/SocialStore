package com.ipca.socialstore.presentation.campaign.listAll

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun ListAllCampaignsView(modifier: Modifier, navController: NavController){

    val viewModel : ListAllCampaignsViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getAllCampaigns()
    }

    ListAllCampaignsContent(
        modifier = Modifier,
        uiState = uiState
    )
}

@Composable
fun ListAllCampaignsContent(
    modifier: Modifier,
    uiState : GetAllCampaignsState,
){

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(
                items = uiState.campaigns,
            ){
                index, campaign -> SingleCampaign(campaign = campaign, modifier = Modifier)
            }
        }
    }
}

@Composable
fun SingleCampaign(
    modifier: Modifier,
    campaign : CampaignModel
){
    Column(
        modifier = Modifier.padding(18.dp)
    ) {
        Text(text = "Nome : ${campaign.name}")
        Text(text = "Nome : ${campaign.date}")
    }
}

@Preview (showBackground = true)
@Composable
fun PreviewListAllCampaigns(){
    SocialStoreTheme() {
        val uiState = GetAllCampaignsState(emptyList(),false,null)
        ListAllCampaignsContent(
            modifier = Modifier,
            uiState = uiState,
        )
    }
}