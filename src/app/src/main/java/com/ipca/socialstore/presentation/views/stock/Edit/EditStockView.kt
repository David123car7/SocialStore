package com.ipca.socialstore.presentation.views.stock.Edit

import android.widget.Button
import android.widget.TableLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockHelper
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.ui.theme.SocialStoreTheme

@Composable
fun EditStockView(navController: NavController, modifier: Modifier){

    val viewModel : EditStockViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    CompleteButton(
        modifier = Modifier,
        onClick = {viewModel.updateEditState()}
    )

    EditButton(
        modifier = Modifier,
        onClick = {viewModel.updateItemInStock()}
    )

}
@Composable
fun EditStockContent(
  modifier: Modifier,
  uiState : EditState
){
    Column(
        modifier.fillMaxSize()
    ) {
        if (uiState.stock != null){
            Row(
                modifier.fillMaxWidth(),
            ) {
                TextField(
                    value = uiState.stock.item.name,
                    label = {Text("Nome Item")},
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth(),
                    onValueChange = {},//funcao que muda o nome item na tabela item
                )
            }
            Row(
                modifier.fillMaxWidth(),
            ) {
                TextField(
                    value = uiState.stock.item.itemType,
                    label = {Text("Tipo Item")},
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth(),
                    onValueChange = {},//funcao que muda o tipo de item na tabela item
                )
            }
            Row(
                modifier.fillMaxWidth(),
            ) {
                TextField(
                    value = uiState.stock.quantity.toString(),//Trocar no model para string
                    label = {Text("Quantidade Total")},
                    modifier = Modifier.padding(8.dp)
                        .fillMaxWidth(),
                    onValueChange = {}, //Funcao que altera quantidade total tenho que forcar a mexer numa quantidade date
                )
            }
            /** Edição das varias quantidades / datas de validade **/
            /** Ter Tabela com as varias datas e quantidades e editar nessa tabela **/
        }
    }
}


@Composable
fun CompleteButton(
    modifier: Modifier,
    onClick : () -> Unit
){
    Button(
        onClick={onClick()}
    ){
        Text("Editar")
    }
}

@Composable
fun EditButton(
    modifier: Modifier,
    onClick : () -> Unit
){
    Button(
        onClick={onClick()}
    ){
        Text("Concluir")
    }
}

@Preview(showBackground = true)
@Composable
fun Preview(){
    SocialStoreTheme() {

    }
}
