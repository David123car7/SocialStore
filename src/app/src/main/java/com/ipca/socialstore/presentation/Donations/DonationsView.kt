package com.ipca.socialstore.presentation.donations

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun DonationsView(modifier: Modifier = Modifier){

    val donationView : DonationsViewModel = hiltViewModel()
    val uiState by donationView.uiState


    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = uiState.donation?.campaign ?:  "",
                label = { Text("Campanha") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { value -> donationView.updateCampaign(value)}
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = itemName,
                label = { Text("Item") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { itemName = it }
            )

            // Campo para a quantidade
            TextField(
                value = itemQuantity,
                label = { Text("Quantidade") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { itemQuantity = it }
            )

        }

        Button(
            onClick = {donationView.updateItem(itemName,itemQuantity)}
        ) {
            Text("Add Item")
        }
        Button(
            onClick = {
                donationView.addDonation()
            }
        ) {
            Text("Add")
        }
    }
}
