package com.ipca.socialstore.presentation.views.donation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme

@Composable
fun CreateDonationView(modifier: Modifier, navController: NavController){

    val viewModel : CreateDonationViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    CreateDonationViewContent(
        modifier,
        uiState,
        onUpdateDate = {newValue -> viewModel.updateDonationDate(newValue)},
        onClickCreate = {viewModel.addDonation()},
        onUpdateName = {newValue -> viewModel.updateItemName(newValue)},
        onUpdateItemType = {newValue -> viewModel.updateItemType(newValue)},
        onUpdateExpiration = {newValue -> viewModel.updateExpirationDate(newValue)},
        onUpdateQuantity = {newValue -> viewModel.updateQuantity(newValue)},
    )
}

@Composable
fun CreateDonationViewContent(
    modifier: Modifier,
    uiState : DonationState,
    onUpdateDate : (newValue : String) -> Unit,
    onUpdateName : (newValue : String) -> Unit,
    onUpdateItemType : (newValue : String) -> Unit,
    onUpdateQuantity : (newValue : String) -> Unit,
    onUpdateExpiration : (newValue : String) -> Unit,
    onClickCreate : () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()

        ) {
            TextField(
                value = uiState.item.name,
                label = { Text("Nome Item") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { value -> onUpdateName(value) })

            TextField(
                value = uiState.item.itemType,
                label = { Text("Tipo Item") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { value -> onUpdateItemType(value) })


        }
        Row(
            modifier = Modifier.fillMaxWidth()

        ) {
            TextField(
                value = uiState.donation.donationDate,
                label = { Text("Data da Campanha") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { value -> onUpdateDate(value) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()

        ) {
            TextField(value = uiState.quantity.toString(),
                label = {Text("Quantidade")},
                modifier = Modifier.padding(8.dp),
                onValueChange = {value -> onUpdateQuantity(value)})

            TextField(value = uiState.expirationDate,
                label = {Text("Data Validade")},
                modifier = Modifier.padding(8.dp),
                onValueChange = {value -> onUpdateExpiration(value)})
        }


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
fun PreviewCreateDonation() { //falta uistate
    SocialStoreTheme() {/*
        CreateDonationViewContent(
            Modifier,
            uiState,
            onUpdateDate = { Unit },
            onClickCreate = { Unit },
            onUpdateQuantity = { Unit},
            onUpdateExpiration = { Unit},
            onUpdateItemType = { Unit},
            onUpdateName = { Unit},)*/
    }
}
