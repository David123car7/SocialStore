package com.ipca.socialstore.presentation.views.stock.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController

import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun GetAllStockView(modifier: Modifier, navController: NavController){

    val viewModel : ListAllStockViewModel = hiltViewModel()
    val uiState by viewModel.uiState
    LaunchedEffect(Unit) {
        val a = viewModel.getAllStock()
        println(a)
    }
    GetAllStockViewContent(
        modifier,
        uiState,
    )
}

@Composable
fun GetAllStockViewContent(
    modifier: Modifier,
    uiState: GetStockState
){

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

    }
}

@Preview(showBackground = true)
@Composable

fun PreviewGetAllStock(){
    SocialStoreTheme() {
        val uiState = GetStockState(null,null,false,null)
        GetAllStockViewContent(Modifier,uiState)
    }
}