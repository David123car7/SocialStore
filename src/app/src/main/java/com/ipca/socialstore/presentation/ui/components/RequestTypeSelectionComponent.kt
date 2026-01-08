package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
    currentSelectionString: String, // Valor atual (ex: "food_clean")
    onSelectionChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Título da Secção
        Text(
            text = "Selecione os Apoios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Iterar sobre cada tipo de pedido
        RequestType.entries.forEach { type ->
            val isSelected = currentSelectionString.split("_").contains(type.code)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp) // Espaçamento vertical generoso
                    // Permite clicar na linha inteira e não só no switch
                    .clickable {
                        val newString = updateRequestTypeString(currentSelectionString, type, !isSelected)
                        onSelectionChange(newString)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Empurra o Switch para a ponta
            ) {
                // Coluna da Esquerda (Texto)
                Column(
                    modifier = Modifier.weight(1f).padding(end = 16.dp)
                ) {
                    Text(
                        text = type.label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    // Se tiveres descrição no teu Enum, descomenta isto:
                    /*
                    Text(
                        text = "Descrição opcional do apoio...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    */
                }

                // Coluna da Direita (Switch)
                Switch(
                    checked = isSelected,
                    onCheckedChange = { isChecked ->
                        val newString = updateRequestTypeString(currentSelectionString, type, isChecked)
                        onSelectionChange(newString)
                    }
                )
            }

            // Linha divisória subtil entre itens (opcional, mas fica bonito)
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
    }
}