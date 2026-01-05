package com.ipca.socialstore.presentation.views.application.listApplications

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.enums.StorageBucket
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.appDocType.GetAppDocTypesUseCase
import com.ipca.socialstore.domain.application.UpdateApplicationDataStateUseCase
import com.ipca.socialstore.domain.services.application.GetAllAplicationsService
import com.ipca.socialstore.domain.services.document.GetApplicationDocumentsService
import com.ipca.socialstore.domain.storage.DownloadFileUseCase
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.files.FileSaveManager
import com.ipca.socialstore.presentation.utils.errors.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val updateApplicationDataStateUseCase: UpdateApplicationDataStateUseCase,
    private val getApplicationDocumentsService: GetApplicationDocumentsService,
    private val getAppDocTypesUseCase: GetAppDocTypesUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val fileSaveManager: FileSaveManager): ViewModel(){
    var uiState = mutableStateOf(ListApplicationsState())

    sealed class DownloadEvent {
        object Loading : DownloadEvent()
        data class Error(val message: String) : DownloadEvent()
        data class Success(val message: String) : DownloadEvent()
        data class PromptUserToSave(val fileName: String) : DownloadEvent()
    }

    private val _downloadEvent = Channel<DownloadEvent>()
    val downloadEvent = _downloadEvent.receiveAsFlow()
    private var pendingFileBytes: ByteArray? = null

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

    fun getApplicationDocuments(applicationId: Int){
        viewModelScope.launch {
            val docBankStatements = getDocuments(
                applicationId = applicationId,
                documentType = DocumentType.BANK_STATEMENTS.folderName
            )

            val docIncomeProof = getDocuments(
                applicationId = applicationId,
                documentType = DocumentType.INCOME_PROOF.folderName
            )
            val docOtherIncome = getDocuments(
                applicationId = applicationId,
                documentType = DocumentType.OTHER_INCOME.folderName
            )
            val docPermanentExpenses = getDocuments(
                applicationId = applicationId,
                documentType = DocumentType.PERMANENT_EXPENSES.folderName
            )
            val docInternationalSupport = getDocuments(
                applicationId = applicationId,
                documentType = DocumentType.INTERNATIONAL_SUPPORT.folderName
            )

            uiState.value = uiState.value.copy(
                documentsBankStatements = docBankStatements,
                documentsOtherIncome = docOtherIncome,
                documentsIncomeProof = docIncomeProof,
                documentsInternationalSupport = docInternationalSupport,
                documentsPermanentExpenses = docPermanentExpenses
            )
            getAppDocTypeStates(applicationId = applicationId)
        }
    }

    suspend fun getDocuments(applicationId: Int,documentType: String): List<DocumentReceiverModel> {
        uiState.value = uiState.value.copy(isLoading = true)
        val documentsResult = getApplicationDocumentsService(applicationId = applicationId, documentType = documentType)
        when(documentsResult){
            is ResultWrapper.Success -> {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                )
                return documentsResult.data
            }
            is ResultWrapper.Error -> {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = documentsResult.error.asUiText()
                )
            }
        }
        return emptyList()
    }

    fun downloadDocument(fileName: String, filePath: String) {
        viewModelScope.launch {
            _downloadEvent.send(DownloadEvent.Loading)
            val result = downloadFileUseCase(filePath = filePath, bucket = StorageBucket.APPLICATION_DOCUMENTS.bucketName)
            when (result) {
                is ResultWrapper.Success -> {
                    pendingFileBytes = result.data
                    _downloadEvent.send(DownloadEvent.PromptUserToSave(fileName))
                }
                is ResultWrapper.Error -> {
                    _downloadEvent.send(DownloadEvent.Error("Erro ao baixar: ${result.error}"))
                    pendingFileBytes = null
                }
            }
        }
    }

    fun saveToUserSelectedUri(uri: Uri) {
        viewModelScope.launch {
            val bytes = pendingFileBytes
            if (bytes != null) {
                val success = fileSaveManager.writeDataToUri(uri, bytes)
                if (success) {
                    _downloadEvent.send(DownloadEvent.Success("Guardado com sucesso!"))
                } else {
                    _downloadEvent.send(DownloadEvent.Error("Falha ao gravar ficheiro."))
                }
                // Cleanup
                pendingFileBytes = null
            } else {
                _downloadEvent.send(DownloadEvent.Error("Ficheiro perdido. Tente novamente."))
            }
        }
    }

    suspend fun getAppDocTypeStates(applicationId: Int) {
        uiState.value = uiState.value.copy(isLoading = true)
        when(val appDocTypesResult = getAppDocTypesUseCase(applicationId = applicationId)){
            is ResultWrapper.Success -> {
                val typesList = appDocTypesResult.data
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    permanentExpensesDocsState = typesList.find { it.type == DocumentType.PERMANENT_EXPENSES.folderName },
                    incomeProofDocsState = typesList.find { it.type == DocumentType.INCOME_PROOF.folderName },
                    bankStatementDocsState = typesList.find { it.type == DocumentType.BANK_STATEMENTS.folderName },
                    otherIncomeDocsState = typesList.find { it.type == DocumentType.OTHER_INCOME.folderName },
                    internationalSupportDocsState = typesList.find { it.type == DocumentType.INTERNATIONAL_SUPPORT.folderName }
                )
            }
            is ResultWrapper.Error -> {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = appDocTypesResult.error.asUiText()
                )
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