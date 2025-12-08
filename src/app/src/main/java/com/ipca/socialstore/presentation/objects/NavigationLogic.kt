package com.ipca.socialstore.presentation.objects

import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes

object NavigationLogic {
    fun navigateTo(navController: NavController, userRole: UserRole, route: Any){
        when(route){
            is GeneralRoutes -> {
                navController.navigate(route = route)
            }
            is BeneficiaryRoutes -> {
                if(userRole == UserRole.BENEFICIARY)
                    navController.navigate(route = route)
                else
                    resetNavigation(navController = navController, userRole = userRole)
            }
            is AdminRoutes -> {
                if(userRole == UserRole.ADMIN)
                    navController.navigate(route = route)
                else
                    resetNavigation(navController = navController, userRole = userRole)
            }
            is DefaultRoutes -> {
                if(userRole == UserRole.DEFAULT)
                    navController.navigate(route = route)
                else
                    resetNavigation(navController = navController, userRole = userRole)
            }
        }
    }

    fun resetNavigation(navController: NavController, userRole: UserRole){
        if(userRole == UserRole.ADMIN)
            navController.navigate(AdminRoutes.AdminHome)
        else if(userRole == UserRole.BENEFICIARY)
            navController.navigate(BeneficiaryRoutes.BeneficiaryHome)
        else if (userRole == UserRole.DEFAULT)
            navController.navigate(DefaultRoutes.DefaultHome)
        else
            navController.navigate(GeneralRoutes.Login)
    }
}