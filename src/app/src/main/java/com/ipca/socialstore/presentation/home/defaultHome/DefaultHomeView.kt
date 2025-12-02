package com.ipca.socialstore.presentation.home.defaultHome

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
import com.ipca.socialstore.presentation.objects.Routes
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun DefaultHomeView(modifier: Modifier, navController: NavController) {
    val homeViewModel: DefaultHomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState


    DefaultHomeViewContent(
        modifier = modifier,
        onClickLogout = {homeViewModel.logout()},
        onClickCreate = {navController.navigate(Routes.CreateDonation)}
    )
}

@Composable
fun DefaultHomeViewContent(modifier: Modifier, onClickLogout:()->Unit, onClickCreate : ()-> Unit){
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
                onClick = {onClickCreate()}
            ) {
                Text("GetItem")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    SocialStoreTheme() {
        DefaultHomeViewContent(modifier = Modifier, onClickLogout = { Unit}, onClickCreate = { Unit})
    }
}