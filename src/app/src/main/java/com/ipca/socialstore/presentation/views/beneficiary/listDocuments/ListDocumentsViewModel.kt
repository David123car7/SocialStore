package com.ipca.socialstore.presentation.views.beneficiary.listDocuments

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.enums.StorageBucket
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.document.GetApplicationDocumentsService
import com.ipca.socialstore.domain.storage.DownloadFileUseCase
import com.ipca.socialstore.domain.usecases.user.GetUserApplicationIdUseCase
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.utils.files.FileSaveManager
import com.ipca.socialstore.presentation.views.application.applicationStateAdmin.ApplicationStateAdminViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListDocumentsState (
    val documentsBankStatements: List<DocumentReceiverModel> = emptyList(),
    val documentsIncomeProof: List<DocumentReceiverModel> = emptyList(),
    val documentsOtherIncome: List<DocumentReceiverModel> = emptyList(),
    val documentsPermanentExpenses: List<DocumentReceiverModel> = emptyList(),
    val documentsInternationalSupport: List<DocumentReceiverModel> = emptyList(),
    val documentsRequirement: List<DocumentReceiverModel> = emptyList(),

    val isLoading: Boolean = false,
    val error: ErrorText? = null,
    val hasApplication: Boolean = true,
)

@HiltViewModel
class ListDocumentsViewModel @Inject constructor(
    private val getApplicationDocumentsService: GetApplicationDocumentsService,
    private val getUserApplicationIdUseCase: GetUserApplicationIdUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val fileSaveManager: FileSaveManager
) : ViewModel(){
    val uiState = mutableStateOf(ListDocumentsState())
    private var pendingFileBytes: ByteArray? = null

    sealed class DownloadEvent {
        object Loading : DownloadEvent()
        data class Error(val message: String) : DownloadEvent()
        data class Success(val message: String) : DownloadEvent()
        data class PromptUserToSave(val fileName: String) : DownloadEvent()
    }

    private val _downloadEvent = Channel<DownloadEvent>()
    val downloadEvent = _downloadEvent.receiveAsFlow()

    init {
        getApplication()
    }
    fun getApplication(){
        viewModelScope.launch {
            val appIdResult = getUserApplicationIdUseCase()
            when(appIdResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        hasApplication = true,
                    )
                    getApplicationDocuments(applicationId = appIdResult.data)
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = appIdResult.error.asUiText()
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

            val requirementDocs = getDocuments(
                applicationId = applicationId,
                documentType = DocumentType.REQUERIMENT.folderName
            )

            uiState.value = uiState.value.copy(
                documentsBankStatements = docBankStatements,
                documentsOtherIncome = docOtherIncome,
                documentsIncomeProof = docIncomeProof,
                documentsInternationalSupport = docInternationalSupport,
                documentsPermanentExpenses = docPermanentExpenses,
                documentsRequirement = requirementDocs
            )
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
            _downloadEvent.send(ListDocumentsViewModel.DownloadEvent.Loading)
            val result = downloadFileUseCase(filePath = filePath, bucket = StorageBucket.APPLICATION_DOCUMENTS.bucketName)
            when (result) {
                is ResultWrapper.Success -> {
                    pendingFileBytes = result.data
                    _downloadEvent.send(ListDocumentsViewModel.DownloadEvent.PromptUserToSave(fileName))
                }
                is ResultWrapper.Error -> {
                    _downloadEvent.send(ListDocumentsViewModel.DownloadEvent.Error("Erro ao baixar: ${result.error}"))
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
                pendingFileBytes = null
            } else {
                _downloadEvent.send(DownloadEvent.Error("Ficheiro perdido. Tente novamente."))
            }
        }
    }
}