package com.ipca.socialstore.presentation.views.application.applicationState

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.ipca.socialstore.data.enums.ApplicationDataStatus
import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.academic.GetAcademicDataUseCase
import com.ipca.socialstore.domain.appDocType.GetAppDocTypesUseCase
import com.ipca.socialstore.domain.appDocType.UpdateAppDocTypeUseCase
import com.ipca.socialstore.domain.application.GetUserApplicationUseCase
import com.ipca.socialstore.domain.applicationState.GetUserApplicationState
import com.ipca.socialstore.domain.applicationState.UpdateApplicationStateUseCase
import com.ipca.socialstore.domain.services.application.DeleteApplicationService
import com.ipca.socialstore.domain.services.application.GetUserApplicationService
import com.ipca.socialstore.domain.services.application.UpdateApplicationService
import com.ipca.socialstore.domain.services.document.DeleteDocumentService
import com.ipca.socialstore.domain.services.document.GetApplicationDocumentsService
import com.ipca.socialstore.domain.services.document.UploadApplicationDocumentsService
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import com.ipca.socialstore.presentation.utils.getFileNameFromUri
import com.ipca.socialstore.presentation.views.application.applicationStateAdmin.createEmptyDocumentTypeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ApplicationState(
    val isLoading: Boolean = false,
    val error: ErrorText? = null,
    val isData: Boolean = true,

    val application: ApplicationModelReceiver = createEmptyApplication(),

    //Files selected
    val selectedBankStatements: List<Uri> = emptyList(),
    val selectedIncomeProof: List<Uri> = emptyList(),
    val selectedOtherIncome: List<Uri> = emptyList(),
    val selectedPermanentExpenses: List<Uri> = emptyList(),
    val selectedInternationalSupport: List<Uri> = emptyList(),

    val documentsBankStatements: List<DocumentReceiverModel> = emptyList(),
    val documentsIncomeProof: List<DocumentReceiverModel> = emptyList(),
    val documentsOtherIncome: List<DocumentReceiverModel> = emptyList(),
    val documentsPermanentExpenses: List<DocumentReceiverModel> = emptyList(),
    val documentsInternationalSupport: List<DocumentReceiverModel> = emptyList(),

    val bankStatementDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val incomeProofDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val otherIncomeDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val permanentExpensesDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    val internationalSupportDocsState: ApplicationDocumentTypeModel = createEmptyDocumentTypeModel(),
    )

@HiltViewModel
class ApplicationStateViewModel @Inject constructor(
    private val getUserApplicationService: GetUserApplicationService,
    private val uploadApplicationDocumentsService: UploadApplicationDocumentsService,
    private val getApplicationDocumentsService: GetApplicationDocumentsService,
    private val deleteDocumentService: DeleteDocumentService,
    private val getAppDocTypesUseCase: GetAppDocTypesUseCase,
    private val deleteApplicationService: DeleteApplicationService,
    private val updateApplicationService: UpdateApplicationService,
    private val updateAppDocTypeUseCase: UpdateAppDocTypeUseCase,
    private val updateApplicationStateUseCase: UpdateApplicationStateUseCase) : ViewModel(){

    var uiState = mutableStateOf(ApplicationState())

    init {
        viewModelScope.launch {
            getApplication()
        }
    }

    fun getApplication(){
        viewModelScope.launch {
            val applicationResult = getUserApplicationService()
            when(applicationResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        application = applicationResult.data,
                        isLoading = false,
                    )
                    getApplicationDocuments(applicationId = uiState.value.application.id!!)
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

    fun updateSchoolYear(value: String) {
        val year = if (value.isBlank()) 0 else value.toIntOrNull() ?: return
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(schoolYear = year)
        )
    }

    fun updateName(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(name = value)
        )
    }

    fun updateBirthDate(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(birthDate = value)
        )
    }

    fun updateCc(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(cc = value)
        )
    }

    fun updatePhoneNumber(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(phoneNumber = value)
        )
    }

    fun updateRequestType(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(requestType = value)
        )
    }

    fun updateTypeCourse(value: String) {
        val currentApp = uiState.value.application
        currentApp.academicData?.let { currentAcademic ->
            uiState.value = uiState.value.copy(
                application = currentApp.copy(
                    academicData = currentAcademic.copy(typeCourse = value)
                )
            )
        }
    }

    fun updateCourse(value: String) {
        val currentApp = uiState.value.application
        currentApp.academicData?.let { currentAcademic ->
            uiState.value = uiState.value.copy(
                application = currentApp.copy(
                    academicData = currentAcademic.copy(course = value)
                )
            )
        }
    }

    fun updateStudentNumber(value: String) {
        val currentApp = uiState.value.application
        currentApp.academicData?.let { currentAcademic ->
            uiState.value = uiState.value.copy(
                application = currentApp.copy(
                    academicData = currentAcademic.copy(studenNumber = value)
                )
            )
        }
    }

    fun updateApplication(){
        val application = ApplicationModel(
            id = uiState.value.application.id,
            name = uiState.value.application.name,
            email = uiState.value.application.email,
            phoneNumber = uiState.value.application.phoneNumber,
            cc = uiState.value.application.cc,
            createdAt = uiState.value.application.createdAt,
            birthDate = uiState.value.application.birthDate,
            requestType = uiState.value.application.requestType,
            schoolYear = uiState.value.application.schoolYear,
            stateId = uiState.value.application.applicationState.id!!,
            academicId = uiState.value.application.academicData?.id,
            dataStateId = uiState.value.application.applicationDataState.id!!
        )

        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val applicationDataState = ApplicationDataStateModel(
                id = uiState.value.application.applicationDataState.id!!,
                state = ApplicationDataStatus.TO_REVIEW.status,
                message = ""
            )
            val updateApplicationResult = updateApplicationService(
                application = application,
                academicModel = uiState.value.application.academicData,
                applicationDataState = applicationDataState
            )
            when (updateApplicationResult) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    updateApplicationDataStateLocal(dataState = applicationDataState)

                    if(checkUpdateApplicationState()){
                        updateApplicationState(ApplicationStates.PENDING.status)
                    }
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = updateApplicationResult.error.asUiText()
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
    fun deleteApplication(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val deleteAppResult =
                deleteApplicationService(
                    applicationId = uiState.value.application.id!!,
                    academicId = uiState.value.application.academicData?.id,
                    applicationStateId = uiState.value.application.applicationState.id!!
                )
            when (deleteAppResult) {
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = deleteAppResult.error.asUiText()
                    )
                }
            }
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

    fun addFiles(uri: Uri?, folderName: String, context: Context) {
        if (uri == null) return

        val fileExists = selectedFileExists(
          fileName = getFileNameFromUri(
                context = context,
                uri = uri),
                folderName = folderName,
                context = context
          )

        if(fileExists){
            uiState.value = uiState.value.copy(
                isLoading = false,
                error = AppError.UnknownError("O ficheiro ja existe").asUiText()
            )
            return
        }

        val currentState = uiState.value

        uiState.value = when (folderName) {
            DocumentType.BANK_STATEMENTS.folderName -> {
                currentState.copy(
                    selectedBankStatements = currentState.selectedBankStatements + uri
                )
            }
            DocumentType.INCOME_PROOF.folderName -> {
                currentState.copy(
                    selectedIncomeProof = currentState.selectedIncomeProof + uri
                )
            }
            DocumentType.OTHER_INCOME.folderName -> {
                currentState.copy(
                    selectedOtherIncome = currentState.selectedOtherIncome + uri
                )
            }
            DocumentType.PERMANENT_EXPENSES.folderName -> {
                currentState.copy(
                    selectedPermanentExpenses = currentState.selectedPermanentExpenses + uri
                )
            }
            DocumentType.INTERNATIONAL_SUPPORT.folderName -> {
                currentState.copy(
                    selectedInternationalSupport = currentState.selectedInternationalSupport + uri
                )
            }
            else -> currentState
        }
    }

    fun removeFile(uri: Uri?, document: DocumentReceiverModel?, folderName: String){
        if(uri != null){
            removeUri(uri = uri, folderName = folderName)
        }
        if(document != null){
            removeSubmittedDocument(document = document)
            removeDocument(document = document, folderName = folderName)
        }
    }

    private fun removeUri(uri: Uri?, folderName: String) {
        if (uri == null) return

        val currentState = uiState.value

        uiState.value = when (folderName) {
            DocumentType.BANK_STATEMENTS.folderName -> {
                currentState.copy(
                    selectedBankStatements = currentState.selectedBankStatements - uri
                )
            }
            DocumentType.INCOME_PROOF.folderName -> {
                currentState.copy(
                    selectedIncomeProof = currentState.selectedIncomeProof - uri
                )
            }
            DocumentType.OTHER_INCOME.folderName -> {
                currentState.copy(
                    selectedOtherIncome = currentState.selectedOtherIncome - uri
                )
            }
            DocumentType.PERMANENT_EXPENSES.folderName -> {
                currentState.copy(
                    selectedPermanentExpenses = currentState.selectedPermanentExpenses - uri
                )
            }
            DocumentType.INTERNATIONAL_SUPPORT.folderName -> {
                currentState.copy(
                    selectedInternationalSupport = currentState.selectedInternationalSupport - uri
                )
            }
            else -> currentState
        }
    }

    private fun removeDocument(document: DocumentReceiverModel, folderName: String) {
        val currentState = uiState.value

        uiState.value = when (folderName) {
            DocumentType.BANK_STATEMENTS.folderName -> {
                currentState.copy(
                    documentsBankStatements = currentState.documentsBankStatements - document
                )
            }
            DocumentType.INCOME_PROOF.folderName -> {
                currentState.copy(
                    documentsIncomeProof = currentState.documentsIncomeProof - document
                )
            }
            DocumentType.OTHER_INCOME.folderName -> {
                currentState.copy(
                    documentsOtherIncome = currentState.documentsOtherIncome - document
                )
            }
            DocumentType.PERMANENT_EXPENSES.folderName -> {
                currentState.copy(
                    documentsPermanentExpenses = currentState.documentsPermanentExpenses - document
                )
            }
            DocumentType.INTERNATIONAL_SUPPORT.folderName -> {
                currentState.copy(
                    documentsInternationalSupport = currentState.documentsInternationalSupport - document
                )
            }
            else -> currentState
        }
    }

    private fun removeSubmittedDocument(document: DocumentReceiverModel) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)
            val result = deleteDocumentService(
                filePath = document.path,
                documentId = document.id!!,
                stateId = document.stateId,
                appDocId = document.appDocId,
            )
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    removeDocument(document = document, document.folderName)
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

    fun submitFile(docTypeStateId: Int, type: String, msg: String, folderName: String, context: Context) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)

            val filesToUpload = when (folderName) {
                DocumentType.BANK_STATEMENTS.folderName -> uiState.value.selectedBankStatements
                DocumentType.INCOME_PROOF.folderName -> uiState.value.selectedIncomeProof
                DocumentType.OTHER_INCOME.folderName -> uiState.value.selectedOtherIncome
                DocumentType.PERMANENT_EXPENSES.folderName -> uiState.value.selectedPermanentExpenses
                DocumentType.INTERNATIONAL_SUPPORT.folderName -> uiState.value.selectedInternationalSupport
                else -> emptyList()
            }

            if (filesToUpload.isNotEmpty()) {
                val result = uploadApplicationDocumentsService(filesList = filesToUpload, folderName = folderName, context = context)
                when(result){
                    is ResultWrapper.Success -> {
                        uiState.value = uiState.value.copy(
                            isLoading = false,
                        )
                        getDocuments2(applicationId = uiState.value.application.id!!, documentType = folderName)
                        resetSelectedFiles(folderName = folderName)
                        updateApplicationDocumentTypeState(
                            id = docTypeStateId,
                            state = ApplicationDocumentTypeState.TO_REVIEW.state,
                            type = type,
                            message = msg
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
            uiState.value = uiState.value.copy(isLoading = false)
        }
    }

    fun resetSelectedFiles(folderName: String) {
        val newState = when (folderName) {
            DocumentType.BANK_STATEMENTS.folderName -> uiState.value.copy(selectedBankStatements = emptyList())
            DocumentType.INCOME_PROOF.folderName -> uiState.value.copy(selectedIncomeProof = emptyList())
            DocumentType.OTHER_INCOME.folderName -> uiState.value.copy(selectedOtherIncome = emptyList())
            DocumentType.PERMANENT_EXPENSES.folderName -> uiState.value.copy(selectedPermanentExpenses = emptyList())
            DocumentType.INTERNATIONAL_SUPPORT.folderName -> uiState.value.copy(selectedInternationalSupport = emptyList())
            else -> uiState.value
        }

        uiState.value = newState
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

                    if(checkUpdateApplicationState()){
                        updateApplicationState(ApplicationStates.PENDING.status)
                    }
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
            else -> currentUiState
        }

        uiState.value = newState
    }

    fun updateApplicationState(state: String) {
        val applicationState = ApplicationStateModel(
            id = uiState.value.application.applicationState.id, state = state
        )
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)
            val result = updateApplicationStateUseCase(applicationState = applicationState)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    updateApplicationStateLocal(applicationState = applicationState)
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

    fun updateApplicationStateLocal(applicationState: ApplicationStateModel) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(
                applicationState = applicationState
            )
        )
    }

    //checks if the application state can be updated
    fun checkUpdateApplicationState(): Boolean {
        val state = uiState.value

        if(state.application.applicationDataState.state == ApplicationDataStatus.DENIED.status)
            return false

        if(state.bankStatementDocsState.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state)
            return false

        if(state.incomeProofDocsState.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state)
            return false

        if(state.otherIncomeDocsState.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state)
            return false

        if(state.permanentExpensesDocsState.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state)
            return false

        if(state.internationalSupportDocsState.state == ApplicationDocumentTypeState.SOMETHING_WRONG.state)
            return false

        return true
    }

    fun clearError(){
        uiState.value = uiState.value.copy(error = null)
    }

    private fun selectedFileExists(fileName: String, folderName: String, context: Context): Boolean{
        if(folderName == DocumentType.BANK_STATEMENTS.folderName){
            for(doc in uiState.value.documentsBankStatements){
                if(doc.name == fileName){
                    return true
                }
            }
            for(uri in uiState.value.selectedBankStatements){
                if(getFileNameFromUri(context = context, uri = uri) == fileName){
                    return true
                }
            }
        }
        return false
    }
}

fun createEmptyApplication(): ApplicationModelReceiver {
    return ApplicationModelReceiver(
        id = null,
        schoolYear = 0,
        name = "",
        birthDate = "",
        createdAt = "",
        cc = "",
        phoneNumber = "",
        email = "",
        requestType = "",

        applicationState = ApplicationStateModel(
            id = null,
            state = ""
        ),

        applicationDataState = ApplicationDataStateModel(
            id = null,
            state = "",
            message = null // String? -> null
        ),

        academicData = null
    )
}