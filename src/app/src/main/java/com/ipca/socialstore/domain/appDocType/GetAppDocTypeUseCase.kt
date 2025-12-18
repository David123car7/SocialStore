package com.ipca.socialstore.domain.appDocType

import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.repository.ApplicationDocumentTypeRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetAppDocTypesUseCase @Inject constructor(private val appDocTypeRep: ApplicationDocumentTypeRepository) {
    suspend operator fun invoke(applicationId: Int): ResultWrapper<List<ApplicationDocumentTypeModel>> {
        return appDocTypeRep.getApplicationDocumentType(applicationId = applicationId)
    }
}