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
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme


@Composable
fun DonationsView(modifier: Modifier = Modifier, navController: NavController){

    val donationView : DonationsViewModel = hiltViewModel()
    val uiState by donationView.uiState

    val item = remember { mutableStateOf(ItemInput()) }

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
                value = item.value.item?.itemName ?: "" ,
                label = { Text("Item") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { value ->
                    val currentInnerModel = item.value.item ?: ItemModel()
                    val updatedInnerModel = currentInnerModel.copy(itemName = value)
                    item.value = item.value.copy(item = updatedInnerModel)
                }
            )
            TextField(
                value = item.value.quantity.toString(),
                label = { Text("Quantidade") },
                modifier = Modifier.padding(8.dp),
                onValueChange = { value ->
                    item.value = item.value.copy(quantity = value.toInt())
                }
            )
        }
        Button(
            onClick = {
                donationView.addItem(item.value.item)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Adiconar Item")
        }
        Button(
            onClick = {
                donationView.addDonation()
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Adiconar Item DB")
        }
    }
}

@Preview
@Composable
fun PreviewDonationsView(){
    SocialStoreTheme() {
    }
}