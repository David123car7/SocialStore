package com.ipca.socialstore.presentation.views.home.defaultHome

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
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.objects.NavigationLogic
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun DefaultHomeView(modifier: Modifier, navController: NavController, userRole: UserRole) {
    val homeViewModel: DefaultHomeViewModel = hiltViewModel()
    val uiState by homeViewModel.uiState

    DefaultHomeViewContent(
        modifier = modifier,
        onClickLogout = {homeViewModel.logout()},
        onClickCreate = {
            NavigationLogic.navigateTo(
            navController = navController,
            userRole = userRole,
            route = AdminRoutes.GetSingleItem
            )
        },
        onClickCreateApplication = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = DefaultRoutes.CreateApplication
            )
        },
        onClickApplication = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = DefaultRoutes.Application
            )
        },
        onClickTest = { navController.navigate(AdminRoutes.GetStock)},
        onClickTest2 = { navController.navigate(AdminRoutes.CreateScheduling)}
    )
}

@Composable
fun DefaultHomeViewContent(
    modifier: Modifier,
    onClickLogout:()->Unit,
    onClickCreate : ()-> Unit,
    onClickApplication : ()-> Unit,
    onClickCreateApplication : ()-> Unit,
    onClickTest : () -> Unit,
    onClickTest2: () -> Unit){
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
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {onClickCreateApplication()}
            ) {
                Text("Create Application")
            }

            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {onClickApplication()}
            ) {
                Text("Application Data")
            }

            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {onClickTest()}
            ) {
                Text("Get Stock")
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {onClickTest2()}
            ) {
                Text("Criar Agendamento")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview(){
    SocialStoreTheme() {
        DefaultHomeViewContent(modifier = Modifier, onClickLogout = { Unit}, onClickCreate = { Unit}, onClickApplication = { Unit}, onClickCreateApplication = {},onClickTest = { Unit}, onClickTest2 = { Unit})
    }
}