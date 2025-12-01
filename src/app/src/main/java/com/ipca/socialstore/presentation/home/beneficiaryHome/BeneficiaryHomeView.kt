package com.ipca.socialstore.presentation.home.beneficiaryHome

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun BenificiaryHomeView(modifier: Modifier){
    BenificiaryHomeContent(modifier = modifier)
}

@Composable
fun BenificiaryHomeContent(modifier: Modifier){

}

@Preview(showBackground = true)
@Composable
fun BenificiaryHomePreview(){
    SocialStoreTheme() {
        BenificiaryHomeContent(modifier = Modifier)
    }
}

