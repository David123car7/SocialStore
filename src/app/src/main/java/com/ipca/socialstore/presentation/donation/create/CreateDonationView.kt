package com.ipca.socialstore.presentation.donation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun CreateDonationView(modifier: Modifier, navController: NavController){

    val viewModel : CreateDonationViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    CreateDonationViewContent(
        modifier,
        uiState,
        onUpdateDate = {newValue -> viewModel.updateDonationDate(newValue)},
        onClickCreate = {viewModel.createDonation()}
    )
}

@Composable
fun CreateDonationViewContent(
    modifier: Modifier,
    uiState : DonationState,
    onUpdateDate : (newValue : String) -> Unit,
    onClickCreate : () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextField(
            value = uiState.donation.donationDate ?: "",
            label = {Text("Data da Campanha")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onUpdateDate(value)}
        )

        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {onClickCreate()}
        ) {
            Text("CreateDonation")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateDonation(){
    SocialStoreTheme() {
        val donation = DonationModel("","")
        val uiState = DonationState(donation = donation,null,false,false)
        CreateDonationViewContent(
            Modifier,
            uiState,
            onUpdateDate = { Unit},
            onClickCreate = { Unit})
    }
}