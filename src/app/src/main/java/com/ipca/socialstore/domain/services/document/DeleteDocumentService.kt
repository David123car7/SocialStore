package com.ipca.socialstore.domain.services.document

import com.ipca.socialstore.data.repository.ApplicationDocumentRepository
import com.ipca.socialstore.data.repository.ApplicationDocumentTypeRepository
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.repository.DocumentStateRepository
import com.ipca.socialstore.data.repository.StorageRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class DeleteDocumentService @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val documentStateRepository: DocumentStateRepository,
    private val appDocRepository: ApplicationDocumentRepository,
    private val storageRepository: StorageRepository) {
    suspend operator fun invoke(filePath: String, documentId: Int, stateId: Int, appDocId: Int): ResultWrapper<Unit> {
        val removeStorageResult = storageRepository.removeDocument(filePath = filePath)
        if(removeStorageResult is ResultWrapper.Error)
            return removeStorageResult

        val deleteAppDocResult = appDocRepository.deleteApplicationDocument(id =  appDocId)
        if(deleteAppDocResult is ResultWrapper.Error)
            return deleteAppDocResult

        val deleteDocumentResult = documentRepository.deleteDocument(documentId = documentId)
        if(deleteDocumentResult is ResultWrapper.Error)
            return deleteDocumentResult

        val deleteDocumentStateResult = documentStateRepository.deleteDocumentState(stateId)
        if(deleteDocumentStateResult is ResultWrapper.Error)
            return deleteDocumentResult

        return ResultWrapper.Success(Unit)
    }
}