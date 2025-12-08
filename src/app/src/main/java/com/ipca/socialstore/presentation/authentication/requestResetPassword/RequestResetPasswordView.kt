package com.ipca.socialstore.presentation.authentication.requestResetPassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.objects.NavigationLogic
import com.ipca.socialstore.presentation.routes.BeneficiaryRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun RequestResetPasswordView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val resetViewModel: RequestResetPasswordViewModel = hiltViewModel()
    val uiState by resetViewModel.uiState

    ResetViewContent(
        modifier = modifier,
        uiState = uiState,
        onEmailUpdate = { email -> resetViewModel.updateEmail(email = email)},
        onClickReset = {resetViewModel.requestResetPassword()}
    )

    LaunchedEffect(uiState.resetRequested) {
        if(uiState.resetRequested){
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route =  GeneralRoutes.ResetPassword)
        }
    }
}

@Composable
fun ResetViewContent(modifier: Modifier, uiState: RequestResetState, onEmailUpdate:(v: String) -> Unit,onClickReset:() -> Unit){
    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = uiState.email,
            label = { Text("Email") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onEmailUpdate(value)})

        if (uiState.error != null) {
            Text(text = uiState.error!!.asString(), modifier = Modifier.padding(8.dp))
        }

        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickReset() }) {
                Text("Reset")
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPreview(){
    SocialStoreTheme() {
        val uiState = RequestResetState(email = "", error = null, isLoading = false)

        ResetViewContent(modifier = Modifier, onClickReset = { Unit}, onEmailUpdate = { Unit}, uiState = uiState)
    }
}