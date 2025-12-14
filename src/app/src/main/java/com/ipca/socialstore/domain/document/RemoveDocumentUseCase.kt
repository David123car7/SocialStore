package com.ipca.socialstore.domain.document

import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class RemoveDocumentUseCase @Inject constructor(private val documentRepository: DocumentRepository) {
    suspend operator fun invoke(documentId: Int): ResultWrapper<Unit> {
        return documentRepository.deleteDocument(documentId = documentId)
    }
}