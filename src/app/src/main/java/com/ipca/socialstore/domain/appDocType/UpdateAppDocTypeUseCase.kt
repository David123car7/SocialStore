package com.ipca.socialstore.domain.appDocType

import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.repository.ApplicationDocumentTypeRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class  UpdateAppDocTypeUseCase @Inject constructor(private val applicationDocumentTypeRepository: ApplicationDocumentTypeRepository){
    suspend operator fun invoke(appDocType: ApplicationDocumentTypeModel): ResultWrapper<Int>{
        return applicationDocumentTypeRepository.updateApplicationDocumentType(applicationDocState = appDocType)
    }
}