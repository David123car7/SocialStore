package com.ipca.socialstore.presentation.application

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.application.UploadApplicationDocument
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
    val selectedFiles: List<Uri> = emptyList(),
)

@HiltViewModel
class ApplicationViewModel @Inject constructor(private val uploadApplicationDocuments: UploadApplicationDocuments): ViewModel() {
    var uiState = mutableStateOf(ApplicationState())

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

    fun submitFile() {
        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)

            if (uiState.value.selectedFiles.isNotEmpty()) {
                val result = uploadApplicationDocuments(uiState.value.selectedFiles)
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