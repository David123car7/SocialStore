package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WarningComponent(tittle: String, message: String?,bgColor: Color, mainColor: Color,icon: ImageVector? = null){
    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, mainColor),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(icon != null){
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = mainColor
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tittle,
                    style = MaterialTheme.typography.titleSmall,
                    color = mainColor,
                    fontWeight = FontWeight.Bold
                )
            }
            if(message != null){
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(
                        top = 4.dp,
                        start = 32.dp
                    )
                )
            }
        }
    }
}