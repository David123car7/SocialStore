package com.ipca.socialstore.presentation.views.application.createApplication

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.services.application.CreateApplicationService
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateApplicationState(
    val isLoading: Boolean = false,
    val error: ErrorText? = null,
    val isSuccess: Boolean = false,
    val isStudent: Boolean = false,
    val application: ApplicationModel = ApplicationModel(schoolYear = 0, name = "", birthDate = "", cc = "", phoneNumber = "", email = "", requestType = "", stateId = -1, academicId = null),
    val academicData: AcademicModel? = null,
    val selectedFiles: List<Uri> = emptyList(),
)

@HiltViewModel
class CreateApplicationViewModel @Inject constructor(
    private val createApplicationService: CreateApplicationService): ViewModel() {
    var uiState = mutableStateOf(CreateApplicationState())

    fun updateIsStudent(state: Boolean){
        if(state){
            uiState.value = uiState.value.copy(academicData = AcademicModel(typeCourse = "", course = "", studenNumber = ""))
        }
        else{
            uiState.value = uiState.value.copy(academicData = null)
        }
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

    fun updateRequestType(value: String) {
        uiState.value = uiState.value.copy(
            application = uiState.value.application.copy(requestType = value)
        )
    }

    fun updateTypeCourse(value: String) {
        if(uiState.value.academicData != null) {
            uiState.value = uiState.value.copy(
                academicData = uiState.value.academicData!!.copy(typeCourse = value)
            )
        }
    }

    fun updateCourse(value: String) {
        if(uiState.value.academicData != null){
            uiState.value = uiState.value.copy(
                academicData = uiState.value.academicData!!.copy(course = value)
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

    fun createApplication(){
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true)

            val result = createApplicationService(uiState.value.application, uiState.value.academicData)
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
}