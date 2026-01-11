package com.ipca.socialstore.presentation.views.reports

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.ItemType
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.reports.GenerateReportDocument
import com.ipca.socialstore.domain.services.reports.GetReportDataService
import com.ipca.socialstore.presentation.models.ReportsHelperModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.utils.files.FileSaveManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ReportsState(
    val reports: List<ReportsHelperModel> = emptyList(),
    val typeData: String? = null,
    var error : ErrorText? = null,
    var isLoading : Boolean = false,
    val showReport : List<ReportsHelperModel> = emptyList(),
    )


@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val generateReportDocument: GenerateReportDocument,
    private val fileSaveManager: FileSaveManager,
    private val getReportDataService: GetReportDataService): ViewModel() {
        val uiState = mutableStateOf(ReportsState())

    init {
        Log.d("App Debug", "GG")
        getReportData()
    }

    fun getReportData(){
        viewModelScope.launch {
            when(val result = getReportDataService()){
                is ResultWrapper.Success ->{
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        reports = result.data,
                        error = null,
                    )
                    Log.d("App Debug", "YES")
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText(),
                    )
                }
            }
        }
    }

    fun generateReport(uri: Uri, itemType: String) {
        if (uiState.value.showReport.isEmpty()) return

        viewModelScope.launch {
            val result = generateReportDocument(itemType = itemType, reportData = uiState.value.showReport)
            when (result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = null,
                    )
                    fileSaveManager.writeContentToUri(uri = uri, content = result.data)
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText(),
                    )
                }
            }
        }
    }

    fun generateFileName(itemType: String): String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = java.time.LocalDate.now().format(formatter)
        val prefix = when (itemType) {
            ItemType.AlIMENTAÇÃO.type -> "RelatorioAlimentacao"
            ItemType.HIGIENE.type -> "RelatorioHigiene"
            else -> "RelatorioLimpeza"
        }
        return "${prefix}_${currentDate}.csv"
    }


    fun updateSearchListType(type: String) {
        val allReports = uiState.value.reports
        if (type.isEmpty() || allReports.isEmpty()) {
            uiState.value = uiState.value.copy(
                showReport = emptyList(),
            )
            return
        }
        val filteredList = allReports.filter { report ->
            report.type.contains(type, ignoreCase = true)
        }
        uiState.value = uiState.value.copy(
            showReport = filteredList,
        )
    }

}