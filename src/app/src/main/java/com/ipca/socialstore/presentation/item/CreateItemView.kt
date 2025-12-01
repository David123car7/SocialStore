package com.ipca.socialstore.presentation.item

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
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun CreateItemView(modifier: Modifier, navController: NavController){

    val viewModel : CreateItemViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    CreateItemViewContent(
        modifier = Modifier,
        uiState = uiState,
        onItemNameUpdate ={value -> viewModel.updateItemName(value)},
        onItemTypeUpdate ={value -> viewModel.updateItemType(value)},
        onClickCreate = { viewModel.createItem(uiState.item) }
    )
}

@Composable
fun CreateItemViewContent(
    modifier: Modifier,
    uiState : ItemState,
    onItemNameUpdate : (newValue : String) -> Unit,
    onItemTypeUpdate : (newValue : String) -> Unit,
    onClickCreate : () -> Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = uiState.item.name,
            label = {Text("Nome Item")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onItemNameUpdate(value)}
        )
        TextField(
            value = uiState.item.itemType ?: "",
            label = {Text("Tipo Item")},
            modifier = Modifier.padding(8.dp),
            onValueChange = {value -> onItemTypeUpdate(value)}
        )
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {onClickCreate()}
        ) {
            Text("CreateItem")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateItem(){
    SocialStoreTheme() {
        val item = ItemModel("","")
        val uiState = ItemState(item,false, error = null,false)
        CreateItemViewContent(
            modifier = Modifier,
            uiState = uiState,
            onItemNameUpdate = { Unit},
            onItemTypeUpdate = { Unit},
            onClickCreate = { Unit})
    }
}