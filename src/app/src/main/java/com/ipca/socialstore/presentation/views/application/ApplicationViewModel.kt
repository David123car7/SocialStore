package com.ipca.socialstore.presentation.views.application

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.application.CreateApplicationUseCase
import com.ipca.socialstore.domain.application.UploadApplicationDocuments
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.plus

data class ApplicationState(
    val isLoading: Boolean = false,
    val error: ErrorText? = null,
    val isSuccess: Boolean = false,
    val isStudent: Boolean = false,
    val application: ApplicationModel = ApplicationModel(schoolYear = 0, name = "", birthDate = "", cc = "", phoneNumber = "", email = "", requestType = "", stateId = null),
    val academicData: AcademicModel = AcademicModel(typeCourse = "", course = "", studenNumber = ""),
    val selectedFiles: List<Uri> = emptyList(),
)

private object DocumentsLimit{
    val firstDocument: Int = 2
}

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val uploadApplicationDocuments: UploadApplicationDocuments): ViewModel() {
    var uiState = mutableStateOf(ApplicationState())

    fun updateIsStudent(state: Boolean){
        uiState.value = uiState.value.copy(isStudent = state)
    }

    fun updateSchoolYear(value: String){
        val year = value.toIntOrNull() ?: return

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

    fun updateEmail(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(email = value)
        )
    }

    fun updateRequestType(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(requestType = value)
        )
    }

    fun updateTypeCourse(value: String) {
        uiState.value = uiState.value.copy(
            academicData = uiState.value.academicData.copy(typeCourse = value)
        )
    }

    fun updateCourse(value: String) {
        uiState.value = uiState.value.copy(
            academicData = uiState.value.academicData.copy(course = value)
        )
    }

    fun updateStudentNumber(value: String) {
        uiState.value = uiState.value.copy(
            academicData = uiState.value.academicData.copy(studenNumber = value)
        )
    }

    fun createApplication(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = createApplicationUseCase(uiState.value.application)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        isSuccess = false,
                        error = result.error.asUiText()
                    )
                }
            }
        }
    }

    fun addFiles(uri: List<Uri>?) {
        if(uri == null || uri.isEmpty())
            return

        val currentList = uiState.value.selectedFiles
        uiState.value = uiState.value.copy(
            selectedFiles = currentList + uri
        )
    }

    fun removeFile(uri: Uri?){
        if(uri == null)
            return

        val newList = uiState.value.selectedFiles - uri
        uiState.value = uiState.value.copy(
            selectedFiles = newList
        )
    }

    fun submiteFileX(){
        submitFile(DocumentsLimit.firstDocument)
    }

    private fun submitFile(maxDocuments: Int) {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)

            if (uiState.value.selectedFiles.isNotEmpty()) {
                val result = uploadApplicationDocuments(uiState.value.selectedFiles, maxDocuments)
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
    }
}