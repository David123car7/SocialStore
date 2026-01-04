package com.ipca.socialstore.presentation.views.Scheduling.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme


@Composable
fun CreateSchedulingView(
    modifier: Modifier,
    navController: NavController
){
    val viewModel : CreateSchedulingViewModel = hiltViewModel()
    val uiState by viewModel.uiState


    CreateSchedulingViewContent(
        modifier = modifier,
        uiState = uiState,

    )
}

@Composable
fun CreateSchedulingViewContent(
    modifier: Modifier = Modifier,
    uiState: CreateSchedulingState,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("Selecionar Beneficiário") }

    Column(modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedText)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    uiState.beneficiaries?.forEach { beneficiary ->
                        DropdownMenuItem(
                            text = { Text("ID: ${beneficiary.id} - ${beneficiary.id ?: ""}") },
                            onClick = {
                                selectedText = "ID: ${beneficiary.id}"
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
fun Preview(){
    SocialStoreTheme() {

    }
}

