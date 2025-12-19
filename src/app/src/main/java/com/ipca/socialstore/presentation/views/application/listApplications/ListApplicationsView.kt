package com.ipca.socialstore.presentation.views.application.listApplications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA

val StatusPendingBg = Color(0xFFFFEebb) // Fundo amarelo claro
val StatusPendingText = Color(0xFFD48806) // Texto amarelo escuro
val StatusApprovedBg = Color(0xFFD6F5D6) // Fundo verde claro
val StatusApprovedText = Color(0xFF237804) // Texto verde escuro
val StatusRejectedBg = Color(0xFFFFCCC7) // Fundo vermelho claro
val StatusRejectedText = Color(0xFFCF1322) // Texto vermelho escuro

@Composable
fun ListApplicationsView(modifier: Modifier, navController: NavController, userRole: UserRole){

    ListApplicationsContent(
        modifier = modifier
    )
}

@Composable
fun ListApplicationsContent(modifier: Modifier){

}
@Composable
fun CandidateCard(
    candidateName: String,
    createdAt: String,
    role: String,
    status: String,
    bgColor: Color,
    textColor: Color,
    onDetailsClick: () -> Unit,
    onValidateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GreenIPCA), // A borda verde característica
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = candidateName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )


                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = status,
                        color = textColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver detalhes", fontSize = 12.sp)
                }

                Button(
                    onClick = onValidateClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenIPCA),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Validar", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun ListApplicationsPreview(){
    ListApplicationsContent(
        modifier = Modifier
    )
}

@Preview(showBackground = true)
@Composable()
fun CandidateCardPreview(){
    Column() {
        CandidateCard(
            candidateName = "David Amorim Carvalho",
            createdAt = "19/08/2025",
            role =  "Estudante: LESI",
            status = "Por Aceitar",
            bgColor = StatusPendingBg,
            textColor = StatusPendingText,
            onDetailsClick = {},
            onValidateClick = {}
        )
        CandidateCard(
            candidateName = "David Amorim Carvalho",
            createdAt = "19/08/2025",
            role =  "Estudante: LESI",
            status = "Aceite",
            bgColor = StatusApprovedBg,
            textColor = StatusApprovedText,
            onDetailsClick = {},
            onValidateClick = {}
        )
        CandidateCard(
            candidateName = "David Amorim Carvalho",
            createdAt = "19/08/2025",
            role =  "Estudante: LESI",
            status = "Negado",
            bgColor = StatusRejectedBg,
            textColor = StatusRejectedText,
            onDetailsClick = {},
            onValidateClick = {}
        )
    }
}

