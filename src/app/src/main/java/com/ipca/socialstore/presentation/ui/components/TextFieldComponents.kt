package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.enums.RequestType
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TextFieldStringComponent(modifier: Modifier, label: String, value: String, icon: ImageVector? = null, onValueUpdate:(newValue: String)->Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueUpdate,
        label = { Text(label) },
        leadingIcon = { if(icon != null) Icon(icon, null)},
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun TextFieldValueComponent(modifier: Modifier, label: String, value: String, icon: ImageVector, onValueUpdate:(newValue: String)->Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueUpdate,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun TextFieldDateComponent(modifier: Modifier, label: String, date: String, onDateUpdate:(newValue: String)->Unit, onDatePickerUpdate:() -> Unit){
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                        onDateUpdate(format.format(date))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    OutlinedTextField(
        value = date,
        onValueChange = onDateUpdate,
        label = { Text(label) },
        placeholder = { Text("DD/MM/AAAA") },
        leadingIcon = { Icon(Icons.Default.DateRange, null) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = !showDatePicker },
        enabled = false,
        readOnly = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun TextFieldPasswordComponent(modifier: Modifier, password: String, onPasswordUpdate:(newValue: String)->Unit) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordUpdate,
        label = { Text("Palavra-passe") },
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder //must change this icons
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SocialStoreDropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    options: List<T>,           // Nova: A lista de opções
    label: String,              // Nova: O texto "Tipo de Pedido"
    getLabel: (T) -> String     // Nova: Como extrair o texto de cada opção
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = if (selectedOption != null) getLabel(selectedOption) else "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(getLabel(item)) },
                    onClick = {
                        onOptionSelected(item)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}