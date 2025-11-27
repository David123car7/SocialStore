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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.ui.theme.SocialStoreTheme


@Composable
fun DonationsView(modifier: Modifier = Modifier, navController: NavController){

    val donationView : DonationsViewModel = hiltViewModel()
    val uiState by donationView.uiState

    val items = remember { mutableStateListOf(ItemsInput()) }

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
        items.forEachIndexed { index, input ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = input.itemName ,
                    label = { Text("Item") },
                    modifier = Modifier.padding(8.dp),
                    onValueChange = { value ->
                        items[index] = items[index].copy(itemName = value)
                    }
                )
                TextField(
                    value = input.itemQuantity.toString(),
                    label = { Text("Quantidade") },
                    modifier = Modifier.padding(8.dp),
                    onValueChange = { value ->
                        items[index] = items[index].copy(itemQuantity = value.toInt())
                    }
                )
            }
        }
        Button(
            onClick = {
                items.add(ItemsInput())
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Adiconar Item")
        }

        Button(
            onClick = {
                items.forEach { item ->
                    if(item.itemName.isNotBlank() && (item.itemQuantity !=  0) ){
                        donationView.updateItem(item.itemName,item.itemQuantity)
                    }
                }
                donationView.addDonation()
            }
        ) {
            Text("Adicionar Doação")
        }
    }
}

@Preview
@Composable
fun PreviewDonationsView(){
    SocialStoreTheme() {
    }
}