package com.ipca.socialstore.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.utils.ui.getApplicationDataStateViewData
import com.ipca.socialstore.presentation.views.application.applicationState.CategoryBox

@Composable
fun ApplicationAdminData(
    application: ApplicationModelReceiver,
    onSubmitMessage: (String) -> Unit,
    onAcceptApplicationData: () -> Unit,
) {
    var showAlertDenyDataBox by remember { mutableStateOf(false) }
    var showAlertAcceptDataBox by remember { mutableStateOf(false) }

    AlertInputComponent(
        show = showAlertDenyDataBox,
        title = "Não Aceitar",
        icon = Icons.Filled.Warning,
        color = Color(0xFFCF1322),
        message = "Escreve o motivo por estes dados não serem aceites.",
        onConfirm = { msg -> onSubmitMessage(msg) },
        onDismiss = { showAlertDenyDataBox = false }
    )

    AlertComponent(
        show = showAlertAcceptDataBox,
        title = "Aceitar",
        icon = Icons.Filled.CheckBox,
        color = GreenIPCA,
        message = "De certeza que queres aceitar esta candidatura?",
        onConfirm = onAcceptApplicationData,
        onDismiss = { showAlertAcceptDataBox = false }
    )

    val uiData = getApplicationDataStateViewData(application.applicationDataState.state)

    CategoryBox(
        title = "Detalhes da Candidatura",
        description = uiData.text,
        descriptionTextColor = uiData.textColor,
        descriptionBgTextColor = uiData.bgColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            SectionTitle("Dados Pessoais")

            InfoRow(label = "Nome Completo", value = application.name, icon = Icons.Default.Person)
            InfoRow(label = "Email", value = application.email, icon = Icons.Default.Email)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    InfoRow(label = "CC", value = application.cc, icon = Icons.Default.Badge)
                }
                Box(Modifier.weight(1f)) {
                    InfoRow(label = "Telemóvel", value = application.phoneNumber, icon = Icons.Default.Phone)
                }
            }
            InfoRow(label = "Data Nasc.", value = application.birthDate, icon = Icons.Default.CalendarToday)

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            if(application.academicData != null){
                SectionTitle("Dados Académicos")
                InfoRow(label = "Numero de Estudante", value = application.academicData.studenNumber, icon = Icons.Default.AccountBox)
                InfoRow(label = "Formação", value = application.academicData.typeCourse, icon = Icons.Default.School)
                InfoRow(label = "Curso", value = application.academicData.course, icon = Icons.Default.MenuBook)
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            SectionTitle("Situação Social")

            InfoRow(label = "Tipo de Pedido", value = application.requestType, icon = Icons.Default.Category)

            StatusRow(
                label = "Apoio FAES",
                isActive = application.faes,
                activeText = "Sim, apoiado pelo fundo",
                inactiveText = "Não possui apoio"
            )

            val hasScholarship = application.scholarShip != null
            StatusRow(
                label = "Bolsa de Estudo",
                isActive = hasScholarship,
                activeText = if (hasScholarship) "Sim (${application.scholarShip.value}€)" else "",
                inactiveText = "Não bolseiro"
            )

            StatusRow(
                label = "Estatuto Internacional",
                isActive = application.offCountry,
                activeText = "Sim",
                inactiveText = "Não (Estudante Nacional)"
            )

            if (application.applicationDataState.state == ApplicationDataStatus.TO_REVIEW.status) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showAlertDenyDataBox = true },
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFEBEE), // Fundo avermelhado claro
                            contentColor = Color(0xFFCF1322)    // Texto vermelho
                        ),
                        border = BorderStroke(1.dp, Color(0xFFCF1322))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Rejeitar")
                    }
                    Button(
                        onClick = { showAlertAcceptDataBox = true },
                        modifier = Modifier.weight(1f).height(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Aceitar")
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationData(
    application: ApplicationModelReceiver,
) {
    val uiData = getApplicationDataStateViewData(application.applicationDataState.state)

    CategoryBox(
        title = "Detalhes da Candidatura",
        description = uiData.text,
        descriptionTextColor = uiData.textColor,
        descriptionBgTextColor = uiData.bgColor
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            SectionTitle("Dados Pessoais")

            InfoRow(label = "Nome Completo", value = application.name, icon = Icons.Default.Person)
            InfoRow(label = "Email", value = application.email, icon = Icons.Default.Email)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) {
                    InfoRow(label = "CC", value = application.cc, icon = Icons.Default.Badge)
                }
                Box(Modifier.weight(1f)) {
                    InfoRow(label = "Telemóvel", value = application.phoneNumber, icon = Icons.Default.Phone)
                }
            }
            InfoRow(label = "Data Nasc.", value = application.birthDate, icon = Icons.Default.CalendarToday)

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            if(application.academicData != null){
                SectionTitle("Dados Académicos")
                InfoRow(label = "Numero de Estudante", value = application.academicData.studenNumber, icon = Icons.Default.AccountBox)
                InfoRow(label = "Formação", value = application.academicData.typeCourse, icon = Icons.Default.School)
                InfoRow(label = "Curso", value = application.academicData.course, icon = Icons.Default.MenuBook)
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

            SectionTitle("Situação Social")

            InfoRow(label = "Tipo de Pedido", value = application.requestType, icon = Icons.Default.Category)

            StatusRow(
                label = "Apoio FAES",
                isActive = application.faes,
                activeText = "Sim, apoiado pelo fundo",
                inactiveText = "Não possui apoio"
            )

            val hasScholarship = application.scholarShip != null
            StatusRow(
                label = "Bolsa de Estudo",
                isActive = hasScholarship,
                activeText = if (hasScholarship) "Sim (${application.scholarShip.value}€)" else "",
                inactiveText = "Não bolseiro"
            )

            StatusRow(
                label = "Estatuto Internacional",
                isActive = application.offCountry,
                activeText = "Sim",
                inactiveText = "Não (Estudante Nacional)"
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StatusRow(label: String, isActive: Boolean, activeText: String, inactiveText: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isActive) GreenIPCA else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(
                text = if (isActive) activeText else inactiveText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) Color.Black else Color.Gray
            )
        }
    }
}