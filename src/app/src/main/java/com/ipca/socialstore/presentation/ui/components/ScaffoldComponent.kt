package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldComponent(
    content :@Composable (PaddingValues) -> Unit
){

    Scaffold(
        topBar = {

            TopAppBar(
                colors = topAppBarColors(
                    containerColor = GreenIPCA,
                    titleContentColor = Color.White,

                ),
                title = {
                    Text("sasocial")
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "falta Coisas",
                )
            }
        },
    ){ innerPadding ->
        content(innerPadding)
    }

}

@Preview(showSystemUi = true, name = "Layout Base com Conteúdo Variável")
@Composable
fun PreviewScaffoldComponent() {
    // Assumindo que você tem seu tema SocialStoreTheme
    SocialStoreTheme {

        // Chamamos o seu componente Wrapper de Layout
        ScaffoldComponent { innerPadding -> // <-- Recebemos o padding calculado

            // Aqui injetamos o CONTEÚDO VARIÁVEL, aplicando o innerPadding
            ContentInjetado(innerPadding)
        }
    }
}

// --- Componente de Conteúdo Variável (Simulação de uma View) ---
@Composable
private fun ContentInjetado(paddingValues: PaddingValues) {
    // Aplicação do padding é CRUCIAL para evitar que o conteúdo se sobreponha às barras
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Aplica o offset do TopBar e BottomBar
            .padding(16.dp) // Adiciona um padding interno extra para o corpo
    ) {
        Text(
            text = "Conteúdo Central da View",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Divider()
        Spacer(Modifier.height(8.dp))
        Text("Este texto está sempre posicionado corretamente, abaixo da TopBar e acima da BottomBar, graças ao PaddingValues.")
        Spacer(Modifier.height(4.dp))
        Text("A TopBar ('sasocial') e a BottomBar ('falta Coisas') são estáticas e consistentes em todas as suas Views.")
    }
}