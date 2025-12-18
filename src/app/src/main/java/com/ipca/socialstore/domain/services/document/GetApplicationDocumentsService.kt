package com.ipca.socialstore.domain.services.document

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.repository.ApplicationDocumentRepository
import com.ipca.socialstore.data.repository.ApplicationDocumentTypeRepository
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.repository.DocumentStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.DocumentReceiverModel
import javax.inject.Inject

class GetApplicationDocumentsService @Inject constructor(
    private val appDocTypeRep: ApplicationDocumentTypeRepository,
    private val appDocRep: ApplicationDocumentRepository,
    private val documentRepository: DocumentRepository,
    private val documentStateRepository: DocumentStateRepository){
    suspend operator fun invoke(applicationId: Int, documentType: String): ResultWrapper<List<DocumentReceiverModel>> {

        val appDocTypeResult = appDocTypeRep.getApplicationDocumentTypeId(applicationId = applicationId, documentType = documentType)
        if(appDocTypeResult is ResultWrapper.Error)
            return ResultWrapper.Error(appDocTypeResult.error)
        val appDocTypeId = (appDocTypeResult as ResultWrapper.Success).data

        val appDocResult = appDocRep.getApplicationDocuments(appDocTypeId = appDocTypeId)
        if(appDocResult is ResultWrapper.Error)
            return ResultWrapper.Error(appDocResult.error)
        val applicationDocuments = (appDocResult as ResultWrapper.Success).data

        val documentIds = applicationDocuments.map { it.documentId }
        val documentStateIds = applicationDocuments.map { it.stateId }

        val documentsResult = documentRepository.getDocuments(ids = documentIds)
        if(documentsResult is ResultWrapper.Error)
            return ResultWrapper.Error(documentsResult.error)
        val documents = (documentsResult as ResultWrapper.Success).data

        val documentStatesResult = documentStateRepository.getDocumentStates(ids = documentStateIds)
        if(documentStatesResult is ResultWrapper.Error)
            return ResultWrapper.Error(documentStatesResult.error)
        val documentStates = (documentStatesResult as ResultWrapper.Success).data

        val documentsMap = documents.associateBy { it.id }
        val statesMap = documentStates.associateBy { it.id }
        val resultList: List<DocumentReceiverModel> = applicationDocuments.map { appDoc ->
            val doc = documentsMap[appDoc.documentId]
            val state = statesMap[appDoc.stateId]
            if (doc != null && state != null) {
                DocumentReceiverModel(
                    id = doc.id,
                    applicationId = applicationId,
                    appDocId = appDoc.id!!,
                    path = doc.path,
                    name = doc.name,
                    folderName = doc.folderName,
                    createdAt = doc.createdAt,
                    stateId = state.id!!,
                    state = state.state,
                    description = state.description
                )
            } else {
                return ResultWrapper.Error(AppError.UnknownError("Error getting application documents"))
            }
        }

        return ResultWrapper.Success(resultList)
    }
}