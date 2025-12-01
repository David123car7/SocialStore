package com.ipca.socialstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.presentation.authentication.login.LoginView
import com.ipca.socialstore.presentation.main.MainViewModel
import com.ipca.socialstore.presentation.authentication.register.RegisterView
import com.ipca.socialstore.presentation.authentication.requestResetPassword.RequestResetPasswordView
import com.ipca.socialstore.presentation.authentication.resetPassword.ResetPasswordView
import com.ipca.socialstore.presentation.campaign.create.CreateCampaingView
import com.ipca.socialstore.presentation.campaign.listAll.ListAllCampaignsView
import com.ipca.socialstore.presentation.home.defaultHome.DefaultHomeView
import com.ipca.socialstore.presentation.objects.Routes
import com.ipca.socialstore.ui.theme.SocialStoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val mainState by mainViewModel.sessionState

            val startDestination = if(mainState.isLoggedIn)
                Routes.DefaultHome
            else
                Routes.Login

            SocialStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = startDestination){
                        composable<Routes.Login>{
                            LoginView(modifier = Modifier.padding(innerPadding),
                                navController = navController)
                        }
                        composable<Routes.Register>{
                            RegisterView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                        composable<Routes.DefaultHome>{
                            DefaultHomeView(modifier = Modifier.padding(innerPadding),
                                navController = navController)
                        }
                        composable<Routes.RequestResetPassword>{
                            RequestResetPasswordView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable<Routes.ResetPassword>{
                            ResetPasswordView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable<Routes.CreateCampaign>{
                            CreateCampaingView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable<Routes.ListAllCampaign>{
                            ListAllCampaignsView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SocialStoreTheme {

    }
}