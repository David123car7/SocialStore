package com.ipca.socialstore.presentation.views.application.applicationStateAdmin

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.enums.StorageBucket
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.DocumentStateModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.appDocType.GetAppDocTypesUseCase
import com.ipca.socialstore.domain.appDocType.UpdateAppDocTypeUseCase
import com.ipca.socialstore.domain.application.UpdateApplicationDataStateUseCase
import com.ipca.socialstore.domain.applicationState.UpdateApplicationStateUseCase
import com.ipca.socialstore.domain.documentState.UpdateDocumentStateUseCase
import com.ipca.socialstore.domain.services.application.AcceptApplicationService
import com.ipca.socialstore.domain.services.application.DenyApplicationService
import com.ipca.socialstore.domain.services.application.GetUserApplicationService
import com.ipca.socialstore.domain.services.document.GetApplicationDocumentsService
import com.ipca.socialstore.domain.services.pdfBox.GenerateDocumentService
import com.ipca.socialstore.domain.storage.DownloadFileUseCase
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText
import com.ipca.socialstore.presentation.utils.files.FileSaveManager
import com.ipca.socialstore.presentation.utils.errors.asUiText
import com.ipca.socialstore.presentation.utils.getRequestTypeDbValue
import com.ipca.socialstore.presentation.views.application.applicationState.createEmptyApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApplicationAdminState(
    val application: ApplicationModelReceiver = createEmptyApplication(),

    val documentsBankStatements: List<DocumentReceiverModel> = emptyList(),
    val documentsIncomeProof: List<DocumentReceiverModel> = emptyList(),
    val documentsOtherIncome: List<DocumentReceiverModel> = emptyList(),
    val documentsPermanentExpenses: List<DocumentReceiverModel> = emptyList(),
    val documentsInternationalSupport: List<DocumentReceiverModel> = emptyList(),
    val documentsRequirement: List<DocumentReceiverModel> = emptyList(),
    val documentsDGES: List<DocumentReceiverModel> = emptyList(),

    val bankStatementDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val incomeProofDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val otherIncomeDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val permanentExpensesDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val internationalSupportDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val documentsRequirementState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val dgesState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),

    val isLoading: Boolean = false,
    val error: ErrorText? = null,
    val appStateUpdated: Boolean = false,
)

@HiltViewModel
class ApplicationStateAdminViewModel @Inject constructor(
    private val getUserApplicationService: GetUserApplicationService,
    private val updateApplicationDataStateUseCase: UpdateApplicationDataStateUseCase,
    private val getApplicationDocumentsService: GetApplicationDocumentsService,
    private val getAppDocTypesUseCase: GetAppDocTypesUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val fileSaveManager: FileSaveManager,
    private val updateDocumentStateUseCase: UpdateDocumentStateUseCase,
    private val updateAppDocTypeUseCase: UpdateAppDocTypeUseCase,
    private val updateApplicationStateUseCase: UpdateApplicationStateUseCase,
    private val acceptApplicationService: AcceptApplicationService,
    private val denyApplicationService: DenyApplicationService,
    private val generateDocumentService: GenerateDocumentService,
    savedStateHandle: SavedStateHandle) : ViewModel(){
    var uiState = mutableStateOf(ApplicationAdminState())
    sealed class DownloadEvent {
        object Loading : DownloadEvent()
        data class Error(val message: String) : DownloadEvent()
        data class Success(val message: String) : DownloadEvent()
        data class PromptUserToSave(val fileName: String) : DownloadEvent()
    }

    private val _downloadEvent = Channel<DownloadEvent>()
    val downloadEvent = _downloadEvent.receiveAsFlow()
    private var pendingFileBytes: ByteArray? = null

    val applicationId: String? = savedStateHandle["applicationId"]

    init {
        viewModelScope.launch {
            getApplication(applicationId?.toIntOrNull())
        }
    }

    fun getApplication(applicationId: Int?) {
        if(applicationId == null) return
        viewModelScope.launch {
            val applicationResult = getUserApplicationService(appId = applicationId)
            when(applicationResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        application = applicationResult.data,
                        isLoading = false,
                    )
                    getApplicationDocuments(applicationId = applicationId)
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = applicationResult.error.asUiText()
                    )
                }
            }
        }
    }

    fun generateRequirementDocument(context: Context){
        val appModel = ApplicationModel(
            id = uiState.value.application.id,
            schoolYear = uiState.value.application.schoolYear,
            name = uiState.value.application.name,
            birthDate = uiState.value.application.birthDate,
            createdAt = uiState.value.application.createdAt,
            cc = uiState.value.application.cc,
            phoneNumber = uiState.value.application.phoneNumber,
            email = uiState.value.application.email,
            requestType = getRequestTypeDbValue(uiState.value.application.requestType),
            offCountry = uiState.value.application.offCountry,
            faes = uiState.value.application.faes,
            stateId = uiState.value.application.applicationState.id!!,
            dataStateId = uiState.value.application.applicationDataState.id!!,
            academicId = uiState.value.application.academicData?.id,
            scholarshipId = uiState.value.application.scholarShip?.id
        )

        viewModelScope.launch {
            val applicationResult = generateDocumentService(
                application = appModel,
                scholarShip = uiState.value.application.scholarShip,
                academicData = uiState.value.application.academicData,
                context = context)
            when(applicationResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    updateApplicationState(state = ApplicationStates.ALMOST_APPROVED.status)
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = applicationResult.error.asUiText()
                    )
                }
            }
        }
    }

    fun updateApplicationDataState(state: String, message: String) {
        val applicationDataStateModel = ApplicationDataStateModel(
            id = uiState.value.application.applicationDataState.id, state = state, message = message
        )
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = updateApplicationDataStateUseCase(applicationDataStateModel)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    updateApplicationDataStateLocal(dataState = applicationDataStateModel)
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

    fun acceptApplication(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val app = ApplicationModel(
                id = uiState.value.application.id,
                name = uiState.value.application.name,
                schoolYear = uiState.value.application.schoolYear,
                cc = uiState.value.application.cc,
                phoneNumber = uiState.value.application.phoneNumber,
                email = uiState.value.application.email,
                requestType = uiState.value.application.requestType,
                academicId = uiState.value.application.academicData?.id,
                birthDate = uiState.value.application.birthDate,
                stateId = uiState.value.application.applicationState.id!!,
                createdAt = uiState.value.application.createdAt,
                dataStateId = uiState.value.application.applicationDataState.id!!,
                offCountry = uiState.value.application.offCountry,
                scholarshipId = uiState.value.application.scholarShip?.id!!,
                faes = uiState.value.application.faes
            )
            val result = acceptApplicationService(app)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        appStateUpdated = true,
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

    fun denyApplication(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = denyApplicationService(
                appStateId = uiState.value.application.applicationState.id!!
            )
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        appStateUpdated = true,
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

    fun updateApplicationDataStateLocal(dataState: ApplicationDataStateModel) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(
                applicationDataState = dataState
            )
        )
    }

    fun updateApplicationState(state: String) {
        val newApplicationState = uiState.value.application.applicationState.copy(state = state)
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = updateApplicationStateUseCase(applicationState = newApplicationState)
            when(result) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        application = uiState.value.application.copy(
                            applicationState = newApplicationState
                        )
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

    fun updateDocumentState(documentId: Int, folderName: String,state: String, message: String) {
        val documentState = DocumentStateModel(id = documentId, state = state, description = message)
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = updateDocumentStateUseCase(documentState)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    getDocuments2(
                        applicationId = uiState.value.application.id!!,
                        documentType = folderName
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

    fun updateApplicationDocumentTypeState(id: Int, state: String, type: String,message: String) {
        val appDocType = ApplicationDocumentTypeModel(
            id = id,
            state = state,
            description = message,
            type = type,
            applicationId = uiState.value.application.id!!
        )
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = updateAppDocTypeUseCase(appDocType)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    updateApplicationDocumentTypeStateLocal(appDocType = appDocType)
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

    fun updateApplicationDocumentTypeStateLocal(appDocType: ApplicationDocumentTypeModel) {
        val currentUiState = uiState.value
        val newState = when (appDocType.type) {
            DocumentType.BANK_STATEMENTS.folderName -> {
                currentUiState.copy(bankStatementDocsState = appDocType)
            }
            DocumentType.INCOME_PROOF.folderName -> {
                currentUiState.copy(incomeProofDocsState = appDocType)
            }
            DocumentType.OTHER_INCOME.folderName -> {
                currentUiState.copy(otherIncomeDocsState = appDocType)
            }
            DocumentType.PERMANENT_EXPENSES.folderName -> {
                currentUiState.copy(permanentExpensesDocsState = appDocType)
            }
            DocumentType.INTERNATIONAL_SUPPORT.folderName -> {
                currentUiState.copy(internationalSupportDocsState = appDocType)
            }
            DocumentType.REQUERIMENT.folderName -> {
                currentUiState.copy(documentsRequirementState = appDocType)
            }
            DocumentType.DGES.folderName -> {
                currentUiState.copy(dgesState = appDocType)
            }
            else -> currentUiState
        }

        uiState.value = newState
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

            val dgesDocs = getDocuments(
                applicationId = applicationId,
                documentType = DocumentType.DGES.folderName
            )

            uiState.value = uiState.value.copy(
                documentsBankStatements = docBankStatements,
                documentsOtherIncome = docOtherIncome,
                documentsIncomeProof = docIncomeProof,
                documentsInternationalSupport = docInternationalSupport,
                documentsPermanentExpenses = docPermanentExpenses,
                documentsRequirement = requirementDocs,
                documentsDGES = dgesDocs
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

    suspend fun getDocuments2(applicationId: Int,documentType: String) {
        uiState.value = uiState.value.copy(isLoading = true)
        val documentsResult = getApplicationDocumentsService(applicationId = applicationId, documentType = documentType)
        when(documentsResult){
            is ResultWrapper.Success -> {
                val newState = when (documentType) {
                    DocumentType.BANK_STATEMENTS.folderName -> uiState.value.copy(documentsBankStatements = documentsResult.data)
                    DocumentType.INCOME_PROOF.folderName -> uiState.value.copy(documentsIncomeProof = documentsResult.data)
                    DocumentType.OTHER_INCOME.folderName -> uiState.value.copy(documentsOtherIncome = documentsResult.data)
                    DocumentType.PERMANENT_EXPENSES.folderName -> uiState.value.copy(documentsPermanentExpenses = documentsResult.data)
                    DocumentType.INTERNATIONAL_SUPPORT.folderName -> uiState.value.copy(documentsInternationalSupport = documentsResult.data)
                    DocumentType.REQUERIMENT.folderName -> uiState.value.copy(documentsRequirement = documentsResult.data)
                    DocumentType.DGES.folderName -> uiState.value.copy(documentsDGES = documentsResult.data)
                    else -> uiState.value
                }

                uiState.value = newState.copy(
                    isLoading = false,
                )
            }
            is ResultWrapper.Error -> {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = documentsResult.error.asUiText()
                )
            }
        }
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

                    permanentExpensesDocsState = typesList.find { it.type == DocumentType.PERMANENT_EXPENSES.folderName }
                        ?: createEmptyDocumentTypeModel(),

                    incomeProofDocsState = typesList.find { it.type == DocumentType.INCOME_PROOF.folderName }
                        ?: createEmptyDocumentTypeModel(),

                    bankStatementDocsState = typesList.find { it.type == DocumentType.BANK_STATEMENTS.folderName }
                        ?: createEmptyDocumentTypeModel(),

                    otherIncomeDocsState = typesList.find { it.type == DocumentType.OTHER_INCOME.folderName }
                        ?: createEmptyDocumentTypeModel(),

                    internationalSupportDocsState = typesList.find { it.type == DocumentType.INTERNATIONAL_SUPPORT.folderName }
                        ?: createEmptyDocumentTypeModel(),

                    documentsRequirementState = typesList.find { it.type == DocumentType.REQUERIMENT.folderName }
                        ?: createEmptyDocumentTypeModel(),

                    dgesState = typesList.find { it.type == DocumentType.DGES.folderName }
                    ?: createEmptyDocumentTypeModel()
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
}

fun createEmptyDocumentTypeModel(): ApplicationDocumentTypeModel {
    return ApplicationDocumentTypeModel(
        id = null,
        applicationId = -1,
        type = "",
        state = "",
        description = null
    )
}