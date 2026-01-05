package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.enums.RequestType
import com.ipca.socialstore.presentation.utils.updateRequestTypeString

@Composable
fun RequestTypeSelection(
    currentSelectionString: String, // O valor que vem da BD (ex: "food_other")
    onSelectionChange: (String) -> Unit // Devolve a nova string combinada
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tipo de Pedido",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        RequestType.entries.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val isSelected = currentSelectionString.split("_").contains(type.code)
                        val newString = updateRequestTypeString(currentSelectionString, type, !isSelected)
                        onSelectionChange(newString)
                    }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = currentSelectionString.split("_").contains(type.code),
                    onCheckedChange = { isChecked ->
                        val newString = updateRequestTypeString(currentSelectionString, type, isChecked)
                        onSelectionChange(newString)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = type.label)
            }
        }
    }
}