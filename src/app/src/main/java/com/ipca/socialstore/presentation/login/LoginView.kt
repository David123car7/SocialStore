package com.ipca.socialstore.presentation.login

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.objects.NavigationViews
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun LoginView(modifier: Modifier, navController: NavController){
    val loginViewModel: LoginViewModel = hiltViewModel()
    val uiState by loginViewModel.uiState

    LoginViewContent(
        modifier = modifier,
        uiState = uiState,
        onEmailUpdate = {value -> loginViewModel.updateEmail(value)},
        onPasswordUpdate = {value -> loginViewModel.updatePassword(value)},
        onLogin = {loginViewModel.login()},
        onClickRegister = {navController.navigate(NavigationViews.register)}
    )
}

@Composable
fun LoginViewContent(modifier: Modifier,
                     uiState: LoginState,
                     onEmailUpdate:(newValue: String)->Unit,
                     onPasswordUpdate:(newValue: String)->Unit,
                     onLogin:()->Unit,
                     onClickRegister:()->Unit){

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
                onClick = { onLogin() }) {
                Text("Login")
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickRegister() }) {
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
        val uiState = LoginState(email = "", password = "", error = "", isLoading = false)

        LoginViewContent(
            modifier = Modifier,
            uiState = uiState,
            onEmailUpdate = { Unit},
            onPasswordUpdate = { Unit},
            onLogin = { Unit},
            onClickRegister = { Unit}
        )
    }
}