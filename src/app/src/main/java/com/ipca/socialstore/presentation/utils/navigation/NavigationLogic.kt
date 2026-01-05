package com.ipca.socialstore.presentation.utils.navigation

import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.CandidateRoutes
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes

object NavigationLogic {
    fun navigateTo(navController: NavController, userRole: UserRole, route: Any) {
        when (route) {
            is GeneralRoutes -> {
                if (route is GeneralRoutes.ResetView) {
                    resetNavigation(navController = navController, userRole = userRole)
                } else {
                    navigateSafe(navController, route)
                }
            }
            is DefaultRoutes -> {
                if (userRole == UserRole.DEFAULT) {
                    navigateSafe(navController, route)
                } else {
                    resetNavigation(navController = navController, userRole = userRole)
                }
            }
            is BeneficiaryRoutes -> {
                if (userRole == UserRole.BENEFICIARY) {
                    navigateSafe(navController, route)
                } else {
                    resetNavigation(navController = navController, userRole = userRole)
                }
            }
            is CandidateRoutes -> {
                if (userRole == UserRole.CANDIDATE) {
                    navigateSafe(navController, route)
                } else {
                    resetNavigation(navController = navController, userRole = userRole)
                }
            }
            is AdminRoutes -> {
                if (userRole == UserRole.ADMIN) {
                    navigateSafe(navController, route)
                } else {
                    resetNavigation(navController = navController, userRole = userRole)
                }
            }
        }
    }

    private fun navigateSafe(navController: NavController, route: Any) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }

    fun resetNavigation(navController: NavController, userRole: UserRole) {
        val destination = when (userRole) {
            UserRole.ADMIN -> AdminRoutes.AdminHome
            UserRole.BENEFICIARY -> BeneficiaryRoutes.BeneficiaryHome
            else -> GeneralRoutes.Home
        }

        navController.navigate(destination) {
            popUpTo(0) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}