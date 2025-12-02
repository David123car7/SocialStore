package com.ipca.socialstore.domain.application

import android.net.Uri
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UploadApplicationDocument @Inject constructor(private val applicationRepository: ApplicationRepository) {
    suspend operator fun invoke(uri: Uri): ResultWrapper<String> {
        return applicationRepository.uploadApplicationDocument(uri = uri)
    }
}