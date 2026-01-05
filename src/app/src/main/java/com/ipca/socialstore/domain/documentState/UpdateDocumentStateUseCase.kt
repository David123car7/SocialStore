package com.ipca.socialstore.domain.documentState

import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.DocumentStateModel
import com.ipca.socialstore.data.repository.CampaignRepository
import com.ipca.socialstore.data.repository.DocumentStateRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class  UpdateDocumentStateUseCase @Inject constructor(private val documentStateRepository: DocumentStateRepository){
    suspend operator fun invoke(docState: DocumentStateModel): ResultWrapper<Int>{
        return documentStateRepository.updateDocumentState(documentState = docState)
    }
}