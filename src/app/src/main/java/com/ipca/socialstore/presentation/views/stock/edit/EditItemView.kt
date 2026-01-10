package com.ipca.socialstore.presentation.views.stock.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.routes.AdminRoutes
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.IconBgColor
import com.ipca.socialstore.presentation.ui.theme.IconTint
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic

@Composable
fun EditItemView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val viewModel : EditItemViewModel = hiltViewModel()
    val uiState by viewModel.uiState

    EditItemContent(
        modifier = modifier,
        uiState = uiState,
        onUpdateName = viewModel::updateName,
        onUpdateBarCode = viewModel::updateBarCode,
        onUpdateType = viewModel::updateItemType,
        onUpdateItem = { viewModel.updateItem() }
    )

    LaunchedEffect(uiState.isEdited) {
        if(uiState.isEdited){
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = AdminRoutes.StockDetails
            )
        }
    }
}

@Composable
fun EditItemContent(
    modifier: Modifier,
    uiState: EditItemState,
    onUpdateName:(String) -> Unit,
    onUpdateType:(String)-> Unit,
    onUpdateBarCode:(String) -> Unit,
    onUpdateItem:() -> Unit){
    Column(
        modifier = modifier.padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Edit, // Ou Icons.Default.Edit
            contentDescription = null,
            tint = IconTint,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = IconBgColor,
                    shape = CircleShape
                )
                .padding(12.dp)
        )

        Text(
            text = "Editar Item",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Preencha os dados que pretende editar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Nome",
            value = uiState.item.name,
            icon = Icons.Outlined.Label,
            onValueUpdate = onUpdateName
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Tipo",
            value = uiState.item.itemType,
            icon = Icons.Outlined.Description,
            onValueUpdate = onUpdateType
        )

        TextFieldStringComponent(
            modifier = Modifier,
            label = "Categoria",
            value = uiState.item.barCode ?: "",
            icon = Icons.Outlined.Category,
            onValueUpdate = onUpdateBarCode
        )

        Button(
            onClick = onUpdateItem,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = buttonColors(GreenIPCA),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Editar", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditItemPreview(){

}