package com.ipca.socialstore.presentation.authentication.register

import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.objects.Routes
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
        onFirstNameUpdate = {value -> registerViewModel.updateFirstName(value)},
        onLastNameUpdate = {value-> registerViewModel.updateLastName(value)},
        onBirthDateUpdate = {value -> registerViewModel.updateBirthDate(value)},
        onRegister = {registerViewModel.register()},
        )

    LaunchedEffect(uiState.isRegistered) {
        if(uiState.isRegistered)
            navController.navigate(Routes.Login)
    }
}

@Composable
fun RegisterViewContent(
    modifier: Modifier,
    uiState: RegisterState,
    onEmailUpdate:(newValue: String)->Unit,
    onFirstNameUpdate:(newValue: String)->Unit,
    onLastNameUpdate:(newValue: String)->Unit,
    onBirthDateUpdate:(newValue: String)->Unit,
    onPasswordUpdate:(newValue: String)->Unit,
    onRegister:()->Unit){

    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = uiState.email,
            label = { Text("Email") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onEmailUpdate(value) }
        )
        TextField(
            value = uiState.password,
            label = { Text("Password") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onPasswordUpdate(value)},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        TextField(
            value = uiState.user.firstName,
            label = { Text("First Name") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onFirstNameUpdate(value) }
        )
        TextField(
            value = uiState.user.lastName,
            label = { Text("Last Name") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onLastNameUpdate(value) }
        )
        TextField(
            value = uiState.user.birthDate,
            label = { Text("Birth Date (YYYY-MM-DD)") },
            modifier = Modifier.padding(8.dp),
            onValueChange = { value -> onBirthDateUpdate(value) }
        )

        if (uiState.error != null) {
            Text(text = uiState.error!!.asString(), modifier = Modifier.padding(8.dp))
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
        val uiState = RegisterState(email = "", password = "", error = null, isLoading = false)

        RegisterViewContent(
            modifier = Modifier,
            uiState = uiState,
            onEmailUpdate = { Unit},
            onPasswordUpdate = { Unit},
            onRegister = { Unit},
            onFirstNameUpdate = { Unit},
            onLastNameUpdate = { Unit},
            onBirthDateUpdate = { Unit}
        )
    }
}
