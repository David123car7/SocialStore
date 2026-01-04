package com.ipca.socialstore.domain.storage

import com.ipca.socialstore.data.repository.StorageRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor(private val storageRepository: StorageRepository) {
    suspend operator fun invoke(filePath: String, bucket: String): ResultWrapper<ByteArray> {
        return storageRepository.downloadFile(bucketName = bucket, filePath = filePath)
    }
}