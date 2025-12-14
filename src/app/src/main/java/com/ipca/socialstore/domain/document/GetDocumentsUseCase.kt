package com.ipca.socialstore.domain.document

import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.repository.CampaignRepository
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetDocumentsUseCase @Inject constructor(private val documentRepository: DocumentRepository){
    suspend operator fun invoke(applicationId: Int, folderName: String) : ResultWrapper<List<DocumentModel>>{
        return documentRepository.getDocuments(applicationId = applicationId, folderName = folderName)
    }
}