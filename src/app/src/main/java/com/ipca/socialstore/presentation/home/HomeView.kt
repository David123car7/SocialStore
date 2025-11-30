package com.ipca.socialstore.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.Navigation
import com.ipca.socialstore.presentation.campaign.create.CreateCampaingViewModel
import com.ipca.socialstore.presentation.objects.NavigationViews
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun HomeView(modifier: Modifier, navController: NavController) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState


    HomeViewContent(
        modifier = modifier,
        onClickLogout = {homeViewModel.logout()},
        onClickAddDonation = {},
        onClickCreateCampaign = {navController.navigate(NavigationViews.createCampaign)},
        onClickListAllCampaigns = {navController.navigate(NavigationViews.listAllCampaign)}
        )
}

@Composable
fun HomeViewContent(modifier: Modifier, onClickLogout:()->Unit, onClickAddDonation:()-> Unit, onClickCreateCampaign:()-> Unit, onClickListAllCampaigns: ()-> Unit){
    Box(modifier = modifier.fillMaxSize()){
        Column(modifier = modifier) {
            Text(modifier = Modifier.padding(8.dp), text = "Home Page")
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickLogout() }) {
                Text("Logout")
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickAddDonation() }) {
                Text("AddDonation")

            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickCreateCampaign() }) {
                Text("CreateCampaign")

            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickListAllCampaigns() }) {
                Text("ListAllCampaign")

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    SocialStoreTheme() {
        HomeViewContent(modifier = Modifier, onClickLogout = { Unit}, onClickAddDonation = { Unit}, onClickCreateCampaign = {Unit}, onClickListAllCampaigns = { Unit})
    }
}