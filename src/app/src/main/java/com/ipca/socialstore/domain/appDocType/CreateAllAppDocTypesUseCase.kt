package com.ipca.socialstore.domain.appDocType

import com.ipca.socialstore.data.enums.ApplicationDocumentTypeState
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.models.ApplicationDocumentTypeModel
import com.ipca.socialstore.data.repository.ApplicationDocumentTypeRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateAllAppDocTypesUseCase @Inject constructor(private val appDocTypeRep: ApplicationDocumentTypeRepository) {
    suspend operator fun invoke(applicationId: Int): ResultWrapper<Unit> {
        DocumentType.entries.forEach { type ->
            val appDocType = ApplicationDocumentTypeModel(
                applicationId = applicationId,
                type = type.folderName,
                state = ApplicationDocumentTypeState.TO_REVIEW.state,
                description = null
            )

            val result = appDocTypeRep.createApplicationDocumentType(appDocType)
            if (result is ResultWrapper.Error) {
                return ResultWrapper.Error(result.error)
            }
        }

        return ResultWrapper.Success(Unit)
    }
}