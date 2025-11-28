package com.ipca.socialstore.presentation.authentication.resetPassword

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
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun ResetPasswordView(modifier: Modifier, navController: NavController){
    val rpViewModel: ResetPasswordViewModel = hiltViewModel()
    val uiState by rpViewModel.uiState

    ResetPasswordContent(
        modifier = modifier,
        uiState = uiState,
        onEmailUpdate = {value -> rpViewModel.updateEmail(value)},
        onPasswordUpdate = {value -> rpViewModel.updatePassword(value)},
        onTokenUpdate = {value -> rpViewModel.updateToken(value)},
        onClickReset = {rpViewModel.resetPassword()}
    )
}

@Composable
fun ResetPasswordContent(
    modifier: Modifier,
    uiState: ResetPasswordState,
    onEmailUpdate:(v: String) -> Unit,
    onPasswordUpdate:(v: String) -> Unit,
    onTokenUpdate:(v: String) -> Unit,
    onClickReset:() -> Unit
    ){

    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = uiState.token,
            label = { Text("Token") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onTokenUpdate(value)}
        )
        TextField(
            value = uiState.email,
            label = { Text("Email") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onEmailUpdate(value)}
        )
        TextField(
            value = uiState.password,
            label = { Text("Password") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onPasswordUpdate(value)}
        )

        if (uiState.error != null) {
            Text(text = uiState.error!!, modifier = Modifier.padding(8.dp))
        }

        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { onClickReset() }) {
                Text("Reset Password")
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
        val uiState = ResetPasswordState(email = "", password = "", token = "", error = "", isLoading = true)
        ResetPasswordContent(modifier = Modifier, uiState = uiState, onClickReset = { Unit}, onTokenUpdate = { Unit}, onPasswordUpdate = { Unit}, onEmailUpdate = { Unit})
    }
}