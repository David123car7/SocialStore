package com.ipca.socialstore.presentation.views.application.listApplications

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.application.UpdateApplicationDataStateUseCase
import com.ipca.socialstore.domain.auth.GetUserSessionStateUseCase
import com.ipca.socialstore.domain.services.application.GetAllAplicationsService
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListApplicationsState(
    val applications: List<ApplicationModelReceiver> = emptyList(),

    val documentsBankStatements: List<DocumentReceiverModel> = emptyList(),
    val documentsIncomeProof: List<DocumentReceiverModel> = emptyList(),
    val documentsOtherIncome: List<DocumentReceiverModel> = emptyList(),
    val documentsPermanentExpenses: List<DocumentReceiverModel> = emptyList(),
    val documentsInternationalSupport: List<DocumentReceiverModel> = emptyList(),

    val bankStatementDocsState: ApplicationDocumentTypeModel? = null,
    val incomeProofDocsState: ApplicationDocumentTypeModel? = null,
    val otherIncomeDocsState: ApplicationDocumentTypeModel? = null,
    val permanentExpensesDocsState: ApplicationDocumentTypeModel? = null,
    val internationalSupportDocsState: ApplicationDocumentTypeModel? = null,

    val isLoading: Boolean = false,
    val error: ErrorText? = null,
)

@HiltViewModel
class ListApplicationsViewModel @Inject constructor(
    private val getAllAplicationsService: GetAllAplicationsService,
    private val updateApplicationDataStateUseCase: UpdateApplicationDataStateUseCase): ViewModel(){
    var uiState = mutableStateOf(ListApplicationsState())

    init {
        getAllApplications()
    }

    fun updateApplicationDataState(id: Int, message: String) {
        val applicationDataStateModel = ApplicationDataStateModel(
            id = id, state = ApplicationDataStatus.DENIED.status, message = message
        )
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = updateApplicationDataStateUseCase(applicationDataStateModel)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }

    fun getAllApplications(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = getAllAplicationsService()
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        applications = result.data
                    )

                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }
}