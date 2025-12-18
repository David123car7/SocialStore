package com.ipca.socialstore.presentation.views.authentication.resetPassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.utils.NavigationLogic
import com.ipca.socialstore.presentation.ui.components.ErrorTextComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldPasswordComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun ResetPasswordView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val resetViewModel: ResetPasswordViewModel = hiltViewModel()
    val uiState by resetViewModel.uiState

    ResetPasswordViewContent(
        modifier = modifier,
        uiState = uiState,
        onEmailUpdate = { email -> resetViewModel.updateEmail(email = email)},
        onClickSendEmail = {resetViewModel.requestResetPassword()},
        onNewPassword1Update = resetViewModel::updateNewPassword1,
        onNewPassword2Update = resetViewModel::updateNewPassword2,
        onTokenUpdate = resetViewModel::updateToken,
        onClickReset = resetViewModel::resetPassword
    )


    LaunchedEffect(uiState.passwordReseted) {
        if(uiState.passwordReseted){
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = NavigationLogic.resetNavigation(
                    navController = navController,
                    userRole = userRole)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordViewContent(
    modifier: Modifier,
    uiState: ResetState,
    onEmailUpdate:(v: String) -> Unit,
    onNewPassword1Update:(v: String) -> Unit,
    onNewPassword2Update:(v: String) -> Unit,
    onTokenUpdate:(v: String) -> Unit,
    onClickSendEmail:() -> Unit,
    onClickReset:() -> Unit){

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Recuperar Conta") },
                navigationIcon = {
                    IconButton(onClick = {
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = if (uiState.requestedReset) Icons.Default.Email else Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(64.dp).padding(bottom = 24.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(
                visible = !uiState.requestedReset,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        "Esqueceu a palavra-passe?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Insira o seu email para receber o código de recuperação.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    TextFieldStringComponent(
                        modifier = Modifier,
                        label = "Email",
                        value = uiState.email,
                        icon = Icons.Default.Star,
                        onValueUpdate = onEmailUpdate
                    )

                    Button(
                        onClick = {
                            onClickSendEmail()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Enviar Código")
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.requestedReset,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Definir Nova Palavra-passe",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Insira o código enviado para ${uiState.email} e a sua nova palavra-passe.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    TextFieldStringComponent(
                        modifier = Modifier,
                        label = "Token",
                        value = uiState.token,
                        icon = Icons.Default.Star,
                        onValueUpdate = onTokenUpdate
                    )

                    Spacer(Modifier.height(16.dp))

                    TextFieldPasswordComponent(
                        modifier = Modifier,
                        password = uiState.newPassword1,
                        onPasswordUpdate = onNewPassword1Update
                    )

                    TextFieldPasswordComponent(
                        modifier = Modifier,
                        password = uiState.newPassword2,
                        onPasswordUpdate = onNewPassword2Update
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onClickReset,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Atualizar Palavra-passe")
                    }

                    Spacer(Modifier.height(16.dp))

                    TextButton(onClick = { onClickSendEmail}) {
                        Text("Voltar / Reenviar Email", color = Color.Gray)
                    }

                    if (uiState.error != null) {
                        ErrorTextComponent(message = uiState.error!!.asString())
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordPreview(){
    SocialStoreTheme() {
        val uiState = ResetState(email = "", error = null, isLoading = false, requestedReset = true)

        ResetPasswordViewContent(modifier = Modifier,
            onClickSendEmail = { Unit},
            onEmailUpdate = { Unit},
            onTokenUpdate = {},
            onClickReset = {},
            onNewPassword1Update = {},
            onNewPassword2Update = {},
            uiState = uiState)
    }
}