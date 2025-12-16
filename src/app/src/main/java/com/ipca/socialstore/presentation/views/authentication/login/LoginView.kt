package com.ipca.socialstore.presentation.views.authentication.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.objects.NavigationLogic
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.components.ErrorTextComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldPasswordComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldValueComponent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun LoginView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val loginViewModel: LoginViewModel = hiltViewModel()
    val uiState by loginViewModel.uiState

    LoginViewContent(
        modifier = modifier,
        uiState = uiState,
        onEmailUpdate = {value -> loginViewModel.updateEmail(value)},
        onPasswordUpdate = {value -> loginViewModel.updatePassword(value)},
        onLogin = {loginViewModel.login()},
        onClickRegister = { NavigationLogic.navigateTo(
            navController = navController,
            userRole = userRole,
            route =  GeneralRoutes.Register
        )},
        onClickReset = {NavigationLogic.navigateTo(
            navController = navController,
            userRole = userRole,
            route =  GeneralRoutes.ResetPassword
        )}
    )

    LaunchedEffect(uiState.isLoggedIn) {
        if(uiState.isLoggedIn){
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = GeneralRoutes.Home
            )
        }
    }
}

@Composable
fun LoginViewContent(modifier: Modifier,
                     uiState: LoginState,
                     onEmailUpdate:(newValue: String)->Unit,
                     onPasswordUpdate:(newValue: String)->Unit,
                     onLogin:()->Unit,
                     onClickRegister:()->Unit,
                     onClickReset:()->Unit){

    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Social Store",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Faça login para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            TextFieldValueComponent(
                modifier = Modifier,
                label = "Email",
                value = uiState.email,
                icon = Icons.Default.Email,
                onValueUpdate = onEmailUpdate
            )

            TextFieldPasswordComponent(
                modifier = Modifier,
                password = uiState.password,
                onPasswordUpdate = onPasswordUpdate
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = onClickReset) {
                    Text("Esqueceu a palavra-passe?", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (uiState.error != null) {
                ErrorTextComponent(message = uiState.error!!.asString())
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ainda não tem conta?", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onClickRegister) {
                    Text("Criar Conta", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginPreview(){
    SocialStoreTheme() {
        val uiState = LoginState(email = "", password = "", error = null, isLoading = false)

        LoginViewContent(
            modifier = Modifier,
            uiState = uiState,
            onEmailUpdate = { Unit},
            onPasswordUpdate = { Unit},
            onLogin = { Unit},
            onClickRegister = { Unit},
            onClickReset = {Unit}
        )
    }
}