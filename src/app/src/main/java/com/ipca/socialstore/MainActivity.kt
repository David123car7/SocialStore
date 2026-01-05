package com.ipca.socialstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ipca.socialstore.presentation.views.authentication.login.LoginView
import com.ipca.socialstore.presentation.main.MainViewModel
import com.ipca.socialstore.presentation.views.authentication.register.RegisterView
import com.ipca.socialstore.presentation.views.campaign.create.CreateCampaingView
import com.ipca.socialstore.presentation.views.campaign.listAll.ListAllCampaignsView
import com.ipca.socialstore.presentation.views.donation.create.CreateDonationView
import com.ipca.socialstore.presentation.views.item.CreateItemView
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.CandidateRoutes
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.SocialStoreScaffold
import com.ipca.socialstore.presentation.views.Scheduling.create.CreateSchedulingView
import com.ipca.socialstore.presentation.views.stock.List.GetAllStockView
import com.ipca.socialstore.presentation.views.stock.List.ListAllStockViewModel
import com.ipca.socialstore.presentation.views.stock.List.StockItemDetailView
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.views.Scheduling.cancelByUser.JustificationScreenView
import com.ipca.socialstore.presentation.views.Scheduling.listAllSchedulingUser.ListAllSchedulingUserView
import com.ipca.socialstore.presentation.views.Scheduling.mainPage.SchedulingMainPageView
import com.ipca.socialstore.presentation.views.Scheduling.management.SchedulingManagementView
import com.ipca.socialstore.presentation.views.Scheduling.management.SchedulingManagementViewModel
import com.ipca.socialstore.presentation.views.application.applicationInfo.ApplicationInfoView
import com.ipca.socialstore.presentation.views.application.applicationState.ApplicationStateView
import com.ipca.socialstore.presentation.views.application.applicationStateAdmin.AplicationStateAdminView
import com.ipca.socialstore.presentation.views.application.applicationStateAdmin.ApplicationStateAdminViewModel
import com.ipca.socialstore.presentation.views.application.createApplication.CreateApplicationView
import com.ipca.socialstore.presentation.views.application.listApplications.ListApplicationsView
import com.ipca.socialstore.presentation.views.authentication.resetPassword.ResetPasswordView
import com.ipca.socialstore.presentation.views.beneficiary.BeneficiaryManagementView
import com.ipca.socialstore.presentation.views.home.adminHome.AdminHomeView
import com.ipca.socialstore.presentation.views.home.beneficiaryHome.BeneficiaryHomeView
import com.ipca.socialstore.presentation.views.home.defaultHomeView.DefaultHomeView
import com.ipca.socialstore.presentation.views.home.testHome.TestHomeView
import com.ipca.socialstore.presentation.views.notification.NotificationHistoryView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val stockViewModel: ListAllStockViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val mainState by mainViewModel.sessionState

            SocialStoreTheme {
                SocialStoreScaffold(navController = navController, userRole = mainState.userRole) { innerPadding ->
                    NavHost(navController = navController, startDestination = GeneralRoutes.Home){
                        composable<GeneralRoutes.Home>{
                            DefaultHomeView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                userRole = mainState.userRole
                            )
                        }
                        composable<GeneralRoutes.TestHome>{
                            TestHomeView(modifier = Modifier.padding(innerPadding),
                                navController = navController, userRole = mainState.userRole)
                        }
                        composable<GeneralRoutes.Login>{
                            LoginView(modifier = Modifier.padding(innerPadding),
                                navController = navController, userRole = mainState.userRole)
                        }
                        composable<GeneralRoutes.Register>{
                            RegisterView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                userRole = mainState.userRole
                            )
                        }
                        composable<DefaultRoutes.ApplicationInfo> {
                            ApplicationInfoView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                userRole = mainState.userRole
                            )
                        }
                        composable<BeneficiaryRoutes.BeneficiaryHome>{
                            BeneficiaryHomeView(
                                modifier = Modifier,
                                userRole = mainState.userRole
                            )
                        }
                        composable<DefaultRoutes.CreateApplication> {
                            CreateApplicationView(modifier = Modifier.padding(innerPadding))
                        }
                        composable<CandidateRoutes.ApplicationState> {
                            ApplicationStateView(modifier = Modifier.padding(innerPadding), navController = navController, userRole = mainState.userRole)
                        }
                        composable<GeneralRoutes.ResetPassword>{
                            ResetPasswordView(modifier = Modifier.padding(innerPadding),
                                navController = navController, userRole = mainState.userRole)
                        }
                        composable<AdminRoutes.AdminHome>{
                            AdminHomeView(modifier = Modifier.padding(innerPadding), navController = navController, userRole = mainState.userRole)
                        }
                        composable<AdminRoutes.ListApplications>{
                            ListApplicationsView(modifier = Modifier.padding(innerPadding), navController = navController, userRole = mainState.userRole)
                        }
                        composable<AdminRoutes.CreateCampaign>{
                            CreateCampaingView(modifier = Modifier.padding(innerPadding))
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
                        composable <AdminRoutes.GetStock>{
                            GetAllStockView(modifier = Modifier.padding(innerPadding), navController = navController, viewModel = stockViewModel)
                        }
                        composable <AdminRoutes.CreateDonation>{
                            CreateDonationView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable(
                            route = AdminRoutes.ApplicationState::class.qualifiedName!! + "/{applicationId}",
                            arguments = listOf(
                                navArgument("applicationId") { type = NavType.StringType }
                            )
                        ) {
                            AplicationStateAdminView(modifier = Modifier.padding(innerPadding), navController = navController, userRole = mainState.userRole)
                        }
                        composable <AdminRoutes.SelectStock>{
                            StockItemDetailView(modifier = Modifier.padding(innerPadding), navController = navController, viewModel = stockViewModel)
                        }
                        composable <AdminRoutes.CreateScheduling>{
                            CreateSchedulingView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable <AdminRoutes.NotificationHistory>{
                            NotificationHistoryView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable <AdminRoutes.SchedulingMainPage>{
                            SchedulingMainPageView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable <AdminRoutes.SchedulingManagement>{
                            SchedulingManagementView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable(
                            route = AdminRoutes.BeneficiaryManagement::class.qualifiedName!! + "/{beneficiaryId}",
                            arguments = listOf(
                                navArgument("beneficiaryId") { type = NavType.StringType }
                            )
                        ) {
                            BeneficiaryManagementView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                        composable <BeneficiaryRoutes.BeneficiaryScheduling>{
                            ListAllSchedulingUserView(modifier = Modifier.padding(innerPadding), navController = navController)
                        }
                        composable(
                            route = BeneficiaryRoutes.JustifyScheduling::class.qualifiedName!! + "/{schedulingId}",
                            arguments = listOf(
                                navArgument("schedulingId") { type = NavType.StringType }
                            )
                        ) {
                            JustificationScreenView(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                    }

                    LaunchedEffect(mainState.isLoggedIn) {
                        NavigationLogic.navigateTo(
                            navController = navController,
                            userRole = mainState.userRole,
                            route = GeneralRoutes.Home
                        )
                    }

                    LaunchedEffect(mainState.userRole) {
                        NavigationLogic.resetNavigation(
                            navController = navController,
                            userRole = mainState.userRole,
                        )
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