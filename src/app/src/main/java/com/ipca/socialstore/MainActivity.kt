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
import com.ipca.socialstore.presentation.donations.DonationsView
import com.ipca.socialstore.presentation.home.HomeView
import com.ipca.socialstore.presentation.authentication.login.LoginView
import com.ipca.socialstore.presentation.main.MainViewModel
import com.ipca.socialstore.presentation.objects.NavigationViews
import com.ipca.socialstore.presentation.authentication.register.RegisterView
import com.ipca.socialstore.presentation.authentication.resetPassword.ResetView
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
                NavigationViews.home
            else
                NavigationViews.login

            SocialStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = startDestination){
                        composable (NavigationViews.login){
                            LoginView(modifier = Modifier.padding(innerPadding),
                                navController = navController)
                        }
                        composable (NavigationViews.register){
                            RegisterView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                        composable (NavigationViews.home){
                            HomeView(modifier = Modifier.padding(innerPadding),
                                navController = navController)
                        }
                        composable ("AddDonation"){
                            DonationsView(modifier = Modifier.padding(innerPadding),
                                navController = navController)
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