package com.ipca.socialstore.presentation.register

import androidx.compose.runtime.getValue
import com.ipca.socialstore.presentation.login.LoginState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.objects.NavigationViews
import com.ipca.socialstore.ui.theme.SocialStoreTheme


@Composable
fun RegisterView(modifier: Modifier, navController: NavController){
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val uiState by registerViewModel.uiState

    RegisterViewContent(
        modifier = modifier,
        uiState = uiState,
        onEmailUpdate = {value -> registerViewModel.updateEmail(value)},
        onPasswordUpdate = {value -> registerViewModel.updatePassword(value)},
        onRegister = {registerViewModel.register()},
    )
}

@Composable
fun RegisterViewContent(modifier: Modifier,
                     uiState: RegisterState,
                     onEmailUpdate:(newValue: String)->Unit,
                     onPasswordUpdate:(newValue: String)->Unit,
                     onRegister:()->Unit){

    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = uiState.email,
            label = { Text("Email") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onEmailUpdate(value) })
        TextField(
            value = uiState.password,
            label = { Text("Password") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onPasswordUpdate(value) })

        if (uiState.error != null) {
            Text(text = uiState.error!!, modifier = Modifier.padding(8.dp))
        }

        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onRegister() }) {
                Text("Register")
            }
        }
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
fun LoginPreview(){
    SocialStoreTheme() {
        val uiState = RegisterState(email = "", password = "", error = "", isLoading = false)

        RegisterViewContent(
            modifier = Modifier,
            uiState = uiState,
            onEmailUpdate = { Unit},
            onPasswordUpdate = { Unit},
            onRegister = { Unit},
        )
    }
}
