package com.ipca.socialstore.presentation.views.application.applicationStateAdmin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.presentation.ui.components.ExpandableSection
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.views.application.listApplications.ApplicationData
import com.ipca.socialstore.presentation.views.application.listApplications.ApplicationDocuments

@Composable
fun AplicationStateAdminView(modifer: Modifier){

}

@Composable
fun ApplicationStateAdminContent(
    modifier: Modifier,
    uiState: ApplicationAdminState,
    onDenyData: (Int, String) -> Unit,
    onDownloadFile:(fileName: String, filePath: String)->Unit){
    var isDataExpanded by remember { mutableStateOf(false) }
    var isDocsExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {  }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Voltar",
                        tint = GreenIPCA
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalhes da Candidatura",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ExpandableSection(
                title = "Dados da candidatura",
                icon = Icons.Outlined.Person,
                isExpanded = isDataExpanded,
                onExpandChange = { isDataExpanded = !isDataExpanded }
            ) {
                ApplicationData(
                    application = uiState.application,
                    onSubmitMessage = onDenyData,
                    bgColor = Color.White
                )
            }

            val documentsCompletedColor = Color(0x120FFC0B)
            val documentsWrongColor = Color(0x1BFF0000)

            ExpandableSection(
                title = "Documentos da candidatura",
                icon = Icons.Outlined.Person,
                isExpanded = isDocsExpanded,
                onExpandChange = { isDocsExpanded = !isDocsExpanded }
            ) {
                var bankStatementsBgColor = Color.White
                var bankStatementsTittle = "Extratos Bancários"
                if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    bankStatementsBgColor = documentsCompletedColor
                    bankStatementsTittle += " (Completo)"
                }
                else if(uiState.bankStatementDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    bankStatementsBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsBankStatements,
                    tittle = bankStatementsTittle,
                    bgColor = bankStatementsBgColor,
                    onDownloadFile = onDownloadFile
                )

                // --- 2. COMPROVATIVOS DE RENDIMENTO ---
                var incomeProofBgColor = Color.White
                var incomeProofTittle = "Comprovativos de Rendimento"
                if(uiState.incomeProofDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    incomeProofBgColor = documentsCompletedColor
                    incomeProofTittle += " (Completo)"
                }
                else if(uiState.incomeProofDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    incomeProofBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsIncomeProof,
                    tittle = incomeProofTittle,
                    bgColor = incomeProofBgColor,
                    onDownloadFile = onDownloadFile
                )

                var otherIncomeBgColor = Color.White
                var otherIncomeTittle = "Outros Rendimentos"
                if(uiState.otherIncomeDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    otherIncomeBgColor = documentsCompletedColor
                    otherIncomeTittle += " (Completo)"
                }
                else if(uiState.otherIncomeDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    otherIncomeBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsOtherIncome,
                    tittle = otherIncomeTittle,
                    bgColor = otherIncomeBgColor,
                    onDownloadFile = onDownloadFile
                )

                var permanentExpensesBgColor = Color.White
                var permanentExpensesTittle = "Despesas Permanentes"
                if(uiState.permanentExpensesDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    permanentExpensesBgColor = documentsCompletedColor
                    permanentExpensesTittle += " (Completo)"
                }
                else if(uiState.permanentExpensesDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    permanentExpensesBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsPermanentExpenses,
                    tittle = permanentExpensesTittle,
                    bgColor = permanentExpensesBgColor,
                    onDownloadFile = onDownloadFile
                )

                var internationalSupportBgColor = Color.White
                var internationalSupportTittle = "Apoio Internacional"
                if(uiState.internationalSupportDocsState?.state == ApplicationDocumentTypeState.COMPLETED.state){
                    internationalSupportBgColor = documentsCompletedColor
                    internationalSupportTittle += " (Completo)"
                }
                else if(uiState.internationalSupportDocsState?.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state){
                    internationalSupportBgColor = documentsWrongColor
                }
                ApplicationDocuments(
                    documentsList = uiState.documentsInternationalSupport,
                    tittle = internationalSupportTittle,
                    bgColor = internationalSupportBgColor,
                    onDownloadFile = onDownloadFile
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable()
fun ApplicationStateAdminPreview(){

}