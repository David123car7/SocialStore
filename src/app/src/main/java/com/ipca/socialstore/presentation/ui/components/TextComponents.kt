package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ErrorTextComponent(message: String){
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error, // Standard Red
        style = MaterialTheme.typography.labelMedium, // Small but readable
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}