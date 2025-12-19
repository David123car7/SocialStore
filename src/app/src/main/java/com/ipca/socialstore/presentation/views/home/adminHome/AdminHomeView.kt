package com.ipca.socialstore.presentation.views.home.adminHome

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ipca.socialstore.presentation.ui.SocialStoreScaffold
import com.ipca.socialstore.presentation.ui.SocialStoreScaffoldContent
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun AdminHomeView(modifier: Modifier){
    AdminHomeContent(modifier = modifier)
}
@Composable
fun AdminHomeContent(modifier: Modifier){

}

@Preview(showBackground = true)
@Composable
fun AdminHomePreview(){
    SocialStoreTheme() {
        AdminHomeContent(modifier = Modifier)
    }
}

