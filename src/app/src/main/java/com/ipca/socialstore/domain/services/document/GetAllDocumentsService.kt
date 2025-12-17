package com.ipca.socialstore.domain.services.document

import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.repository.DocumentStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import javax.inject.Inject

class GetAllDocumentsService @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val documentStateRepository: DocumentStateRepository){
    suspend operator fun invoke(applicationId: Int): ResultWrapper<List<DocumentReceiverModel>> {
        val documentsResult = documentRepository.getAllDocuments(applicationId = applicationId)
        if(documentsResult is ResultWrapper.Error)
            return ResultWrapper.Error(documentsResult.error)
        val documents = (documentsResult as ResultWrapper.Success).data

        val documentsList = mutableListOf<DocumentReceiverModel>()
        for(doc in documents){
            val documentsStateResult = documentStateRepository.getDocumentState(doc.stateId)

            if(documentsStateResult is ResultWrapper.Error)
                return ResultWrapper.Error(documentsStateResult.error)
            val documentState = (documentsStateResult as ResultWrapper.Success).data

            val documentReceiver = DocumentReceiverModel(
                id = doc.id,
                path = doc.path,
                name = doc.name,
                folderName = doc.folderName,
                stateId = doc.stateId ,
                applicationId = doc.applicationId,
                state = documentState.state,
                created_at = doc.createdAt,
                description = documentState.description
            )
            documentsList.add(documentReceiver)
        }

        return ResultWrapper.Success(documentsList)
    }
}