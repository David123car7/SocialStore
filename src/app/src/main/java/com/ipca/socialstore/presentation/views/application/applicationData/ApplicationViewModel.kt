package com.ipca.socialstore.presentation.views.application.applicationData

import android.content.Context


import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.academic.GetAcademicDataUseCase
import com.ipca.socialstore.domain.application.GetUserApplicationUseCase
import com.ipca.socialstore.domain.applicationState.GetUserApplicationState
import com.ipca.socialstore.domain.document.GetAllDocumentsUseCase
import com.ipca.socialstore.domain.services.DeleteDocumentService
import com.ipca.socialstore.domain.storage.document.UploadDocumentsUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.plus

data class ApplicationState(
    val isLoading: Boolean = false,
    val error: ErrorText? = null,
    val isData: Boolean = true,

    val application: ApplicationModel? = null,
    val applicationState: ApplicationStateModel? = null,
    val academicData: AcademicModel? = null,

    //Files ()
    val selectedBankStatements: List<Uri> = emptyList(),
    val selectedIncomeProof: List<Uri> = emptyList(),
    val selectedOtherIncome: List<Uri> = emptyList(),
    val selectedPermanentExpenses: List<Uri> = emptyList(),
    val selectedInternationalSupport: List<Uri> = emptyList(),

    //Documents
    val documentsBankStatements: List<DocumentModel> = emptyList(),
    val documentsIncomeProof: List<DocumentModel> = emptyList(),
    val documentsOtherIncome: List<DocumentModel> = emptyList(),
    val documentsPermanentExpenses: List<DocumentModel> = emptyList(),
    val documentsInternationalSupport: List<DocumentModel> = emptyList()
)

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val getUserApplicationUseCase: GetUserApplicationUseCase,
    private val uploadDocumentsUseCase: UploadDocumentsUseCase,
    private val getAcademicDataUseCase: GetAcademicDataUseCase,
    private val getUserApplicationState: GetUserApplicationState,
    private val getAllDocumentsUseCase: GetAllDocumentsUseCase,
    private val deleteDocumentService: DeleteDocumentService): ViewModel() {
    var uiState = mutableStateOf(ApplicationState())

    init {
        if(uiState.value.application == null){
            viewModelScope.launch {
                uiState.value = uiState.value.copy(isLoading = true)
                when(val applicationResult = getUserApplicationUseCase()){
                    is ResultWrapper.Success -> {
                        uiState.value = uiState.value.copy(
                            application =  applicationResult.data,
                            isLoading = false,
                        )
                        val applicationStateResult = getUserApplicationState(applicationId = uiState.value.application?.stateId!!)
                        when(applicationStateResult){
                            is ResultWrapper.Success -> {
                                uiState.value = uiState.value.copy(
                                    applicationState = applicationStateResult.data,
                                    isLoading = false,
                                )
                                getAllDocuments()
                            }
                            is ResultWrapper.Error -> {
                                uiState.value = uiState.value.copy(
                                    isLoading = false,
                                    error = applicationStateResult.error.asUiText()
                                )
                            }
                        }

                        if(applicationResult.data.academicId != null){
                            val academicResult = getAcademicDataUseCase(applicationResult.data.academicId)
                            when(academicResult){
                                is ResultWrapper.Success -> {
                                    uiState.value = uiState.value.copy(
                                        academicData = academicResult.data,
                                        isLoading = false,
                                    )
                                }
                                is ResultWrapper.Error -> {
                                    uiState.value = uiState.value.copy(
                                        isLoading = false,
                                        error = academicResult.error.asUiText()
                                    )
                                }
                            }
                        }
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
    }

    suspend fun getAllDocuments() {
        if(uiState.value.application!= null){
            uiState.value = uiState.value.copy(isLoading = true)
            val documentsResult = getAllDocumentsUseCase(applicationId = uiState.value.application?.id!!)
            when(documentsResult){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                    filterDocuments(documentsResult.data)
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = documentsResult.error.asUiText()
                    )
                }
            }
        }
    }

    private fun filterDocuments(documentsList: List<DocumentModel>){
        uiState.value = uiState.value.copy(
            documentsBankStatements = documentsList.filter { doc ->
                doc.folderName == DocumentType.BANK_STATEMENTS.folderName
            },

            documentsIncomeProof = documentsList.filter { doc ->
                doc.folderName == DocumentType.INCOME_PROOF.folderName
            },

            documentsOtherIncome = documentsList.filter { doc ->
                doc.folderName == DocumentType.OTHER_INCOME.folderName
            },

            documentsPermanentExpenses = documentsList.filter { doc ->
                doc.folderName == DocumentType.PERMANENT_EXPENSES.folderName
            },

            documentsInternationalSupport = documentsList.filter { doc ->
                doc.folderName == DocumentType.INTERNATIONAL_SUPPORT.folderName
            }
        )
    }

    fun addFiles(uris: List<Uri>, folderName: String) {
        if (uris.isEmpty()) return

        val currentState = uiState.value

        uiState.value = when (folderName) {
            DocumentType.BANK_STATEMENTS.folderName -> {
                currentState.copy(
                    selectedBankStatements = currentState.selectedBankStatements + uris
                )
            }
            DocumentType.INCOME_PROOF.folderName -> {
                currentState.copy(
                    selectedIncomeProof = currentState.selectedIncomeProof + uris
                )
            }
            DocumentType.OTHER_INCOME.folderName -> {
                currentState.copy(
                    selectedOtherIncome = currentState.selectedOtherIncome + uris
                )
            }
            DocumentType.PERMANENT_EXPENSES.folderName -> {
                currentState.copy(
                    selectedPermanentExpenses = currentState.selectedPermanentExpenses + uris
                )
            }
            DocumentType.INTERNATIONAL_SUPPORT.folderName -> {
                currentState.copy(
                    selectedInternationalSupport = currentState.selectedInternationalSupport + uris
                )
            }
            else -> currentState
        }
    }

    fun removeSelectedFile(uri: Uri?, folderName: String) {
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

    fun removeDocument(document: DocumentModel, folderName: String) {
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
                    documentsIncomeProof = currentState.documentsOtherIncome - document
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

    fun removeSubmittedDocument(document: DocumentModel) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)
            val result = deleteDocumentService(filePath = document.path, documentId = document.id!!)
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

    fun submitFiles(folderName: String, context: Context) {
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
                val result = uploadDocumentsUseCase(filesList = filesToUpload, folderName = folderName, context = context)
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

            uiState.value = uiState.value.copy(isLoading = false)
        }
    }

    fun updateIsData(){
        val data = !uiState.value.isData
        uiState.value = uiState.value.copy(isData = data)
    }

    fun updateSchoolYear(value: String){
        val year = value.toIntOrNull() ?: return

        uiState.value = uiState.value.copy(
            application = uiState.value.application?.copy(schoolYear = year)
        )
    }

    fun updateName(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application?.copy(name = value)
        )
    }

    fun updateBirthDate(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application?.copy(birthDate = value)
        )
    }

    fun updateCc(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application?.copy(cc = value)
        )
    }

    fun updatePhoneNumber(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application?.copy(phoneNumber = value)
        )
    }

    fun updateRequestType(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application?.copy(requestType = value)
        )
    }

    fun updateTypeCourse(value: String) {
        if(uiState.value.academicData != null) {
            uiState.value = uiState.value.copy(
                academicData = uiState.value.academicData?.copy(typeCourse = value)
            )
        }
    }

    fun updateCourse(value: String) {
        if(uiState.value.academicData != null){
            uiState.value = uiState.value.copy(
                academicData = uiState.value.academicData?.copy(course = value)
            )
        }
    }

    fun updateStudentNumber(value: String) {
        if(uiState.value.academicData != null) {
            uiState.value = uiState.value.copy(
                academicData = uiState.value.academicData!!.copy(studenNumber = value)
            )
        }
    }
}