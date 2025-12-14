package com.ipca.socialstore.domain.document

import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetAllDocumentsUseCase @Inject constructor(private val documentRepository: DocumentRepository){
    suspend operator fun invoke(applicationId: Int) : ResultWrapper<List<DocumentModel>>{
        return documentRepository.getAllDocuments(applicationId = applicationId)
    }
}