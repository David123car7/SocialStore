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
import com.ipca.socialstore.presentation.application.ApplicationView
import com.ipca.socialstore.presentation.authentication.login.LoginView
import com.ipca.socialstore.presentation.main.MainViewModel
import com.ipca.socialstore.presentation.authentication.register.RegisterView
import com.ipca.socialstore.presentation.authentication.requestResetPassword.RequestResetPasswordView
import com.ipca.socialstore.presentation.authentication.resetPassword.ResetPasswordView
import com.ipca.socialstore.presentation.campaign.create.CreateCampaingView
import com.ipca.socialstore.presentation.campaign.listAll.ListAllCampaignsView
import com.ipca.socialstore.presentation.donation.create.CreateDonationView
import com.ipca.socialstore.presentation.home.defaultHome.DefaultHomeView
import com.ipca.socialstore.presentation.item.CreateItemView
import com.ipca.socialstore.presentation.item.getSingleItem.GetSingleItemView
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.routes.NoRoleRoutes
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
                NoRoleRoutes.DefaultHome
            else
                GeneralRoutes.Login

            SocialStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = startDestination){
                        composable<GeneralRoutes.Login>{
                            LoginView(modifier = Modifier.padding(innerPadding),
                                navController = navController, userRole = mainState.userRole)
                        }
                        composable<GeneralRoutes.Register>{
                            RegisterView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController, userRole = mainState.userRole
                            )
                        }
                        composable<NoRoleRoutes.Application> {
                            ApplicationView(modifier = Modifier.padding(innerPadding))
                        }
                        composable<NoRoleRoutes.DefaultHome>{
                            DefaultHomeView(modifier = Modifier.padding(innerPadding),
                                navController = navController, userRole = mainState.userRole)
                        }
                        composable<GeneralRoutes.RequestResetPassword>{
                            RequestResetPasswordView(modifier = Modifier.padding(innerPadding),
                                navController = navController, userRole = mainState.userRole)
                        }
                        composable<GeneralRoutes.ResetPassword>{
                            ResetPasswordView(modifier = Modifier.padding(innerPadding))
                        }
                        composable<AdminRoutes.CreateCampaign>{
                            CreateCampaingView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable<AdminRoutes.ListAllCampaign>{
                            ListAllCampaignsView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable <AdminRoutes.CreateItem>{
                            CreateItemView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable <AdminRoutes.CreateDonation>{
                            CreateDonationView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable <AdminRoutes.GetSingleItem>{
                            GetSingleItemView(modifier = Modifier.padding(innerPadding), navController = navController)
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