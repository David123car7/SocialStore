package com.ipca.socialstore.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipca.socialstore.presentation.objects.NavigationViews
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun HomeView(modifier: Modifier, navController: NavController) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState

    HomeViewContent(modifier = modifier)

    LaunchedEffect(uiState.isLoggedIn) {
        homeViewModel.getUserStateSession()
        if(!uiState.isLoggedIn){
            navController.navigate(NavigationViews.login)
        }
    }
}

@Composable
fun HomeViewContent(modifier: Modifier){
    Box(modifier = modifier.fillMaxSize()){
        Text("Home Page")
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    SocialStoreTheme() {
        HomeViewContent(modifier = Modifier)
    }
}