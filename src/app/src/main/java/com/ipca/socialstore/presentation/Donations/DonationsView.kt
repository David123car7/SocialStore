package com.ipca.socialstore.presentation.Donations

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ipca.socialstore.Models.DonationModel
import com.ipca.socialstore.presentation.donations.DonationsViewModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import org.w3c.dom.Text
import java.sql.Date

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
