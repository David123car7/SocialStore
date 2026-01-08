package com.ipca.socialstore.presentation.views.authentication.register

import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.components.ErrorTextComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldPasswordComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.IconBgColor
import com.ipca.socialstore.presentation.ui.theme.IconTint
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegisterView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val uiState by registerViewModel.uiState

    RegisterViewContent(
        modifier = modifier,
        uiState = uiState,
        onEmailUpdate = {value -> registerViewModel.updateEmail(value)},
        onPasswordUpdate = {value -> registerViewModel.updatePassword(value)},
        onLogin = {
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = GeneralRoutes.Login)},
        onRegister = {registerViewModel.register()},
        )

    LaunchedEffect(uiState.isRegistered) {
        if(uiState.isRegistered)
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route =  GeneralRoutes.Login
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class) //Wtf is this?
@Composable
fun RegisterViewContent(
    modifier: Modifier,
    uiState: RegisterState,
    onEmailUpdate:(newValue: String)->Unit,
    onPasswordUpdate:(newValue: String)->Unit,
    onLogin:() -> Unit,
    onRegister:()->Unit){

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = IconBgColor
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    tint = IconTint
                )
            }

            Text(
                text = "Criar Conta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = GreenIPCA
            )

            Text(
                text = "Preencha os dados para se registar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            TextFieldStringComponent(
                modifier = Modifier,
                label = "Email",
                value = uiState.email,
                icon = Icons.Outlined.Email,
                onValueUpdate = onEmailUpdate
            )

            TextFieldPasswordComponent(
                modifier = Modifier,
                password = uiState.password,
                onPasswordUpdate = onPasswordUpdate
            )

            Button(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = buttonColors(GreenIPCA)
            ) {
                Text("Registar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (uiState.error != null) {
                ErrorTextComponent(message = uiState.error!!.asString())
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Já tem conta?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onLogin) {
                    Text("Entrar", color = GreenIPCA, fontWeight = FontWeight.Bold)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
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
            onLogin = { Unit},
        )
    }
}
