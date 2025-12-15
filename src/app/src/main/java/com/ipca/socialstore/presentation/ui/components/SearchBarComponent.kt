package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBarContent(
    onSearchItem: (value: String) -> Unit,
){
    val searchValueState = remember { mutableStateOf("") }
    val searchValue by searchValueState

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchValue,
            label = { Text("Pesquisar item", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Ícone de Pesquisa", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            modifier = Modifier.weight(1f),
            onValueChange = { newValue ->
                searchValueState.value = newValue
                onSearchItem(newValue)
            },
            singleLine = true,
            shape = MaterialTheme.shapes.small
        )
    }
}