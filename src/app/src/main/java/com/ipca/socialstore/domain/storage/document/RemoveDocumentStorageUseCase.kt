package com.ipca.socialstore.domain.storage.document

import com.ipca.socialstore.data.repository.StorageRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class RemoveDocumentStorageUseCase @Inject constructor(private val storageRepository: StorageRepository){
    suspend operator fun invoke(filePath: String) : ResultWrapper<Unit>{
        return storageRepository.removeDocument(filePath = filePath)
    }
}