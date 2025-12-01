package com.ipca.socialstore.presentation.item.getSingleItem

import android.app.PictureInPictureUiState
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
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun GetSingleItemView(modifier: Modifier, navController: NavController){

    val viewModel : GetSingleItemViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    GetSingleItemContent(
        modifier = Modifier,
        uiState = uiState,
        onUpdateItemId = {value -> viewModel.updateSearchId(value)},
        onSearchItem = { viewModel.getSingleItem() }
    )

}

@Composable
fun GetSingleItemContent(
    modifier: Modifier,
    uiState: GetItemState,
    onUpdateItemId : (newValue : String ) -> Unit,
    onSearchItem : () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = uiState.itemId ?: "",
            label = {Text("Item Id")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onUpdateItemId(value) }
        )
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {onSearchItem()}
        ) {
            Text("GetItem")
        }

        if (uiState.item != null) {
            SingleItem(
                modifier = Modifier,
                uiState.item
            )
        }else{
            Text("Nao exite nada")
        }
    }
}

@Composable
fun SingleItem(
    modifier: Modifier,
    item : ItemModel
){
    Column(
        modifier = Modifier.padding(18.dp)
    ) {
        Text(text = "Nome : ${item.name}")
        Text(text = "Tipo Item : ${item.itemType}")
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewGetSingleItem(){
    SocialStoreTheme() {
        val item = ItemModel("","")
        val uiState = GetItemState(item,false,null,"")
        GetSingleItemContent(
            modifier = Modifier,
            uiState = uiState,
            onUpdateItemId = { Unit},
            onSearchItem = { Unit}
        )
    }
}