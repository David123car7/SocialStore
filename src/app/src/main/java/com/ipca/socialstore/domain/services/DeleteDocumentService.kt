package com.ipca.socialstore.domain.services

import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.document.RemoveDocumentUseCase
import com.ipca.socialstore.domain.storage.document.RemoveDocumentStorageUseCase
import javax.inject.Inject

class DeleteDocumentService @Inject constructor(
    private val removeDocumentUseCase: RemoveDocumentUseCase,
    private val removeDocumentStorageUseCase: RemoveDocumentStorageUseCase) {
    suspend operator fun invoke(filePath: String, documentId: Int): ResultWrapper<Unit> {
        val removeStorageResult = removeDocumentStorageUseCase(filePath = filePath)
        if(removeStorageResult is ResultWrapper.Error)
            return removeStorageResult

        return removeDocumentUseCase(documentId = documentId)
    }
}