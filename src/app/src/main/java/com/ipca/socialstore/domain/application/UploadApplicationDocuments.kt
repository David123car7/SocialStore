package com.ipca.socialstore.domain.application

import android.net.Uri
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UploadApplicationDocuments @Inject constructor(private val applicationRepository: ApplicationRepository) {
    suspend operator fun invoke(uris: List<Uri>, limit: Int): ResultWrapper<List<String>> {
        if(uris.count() > limit)
            return ResultWrapper.Error(AppError.InvalidFilesNumber)

        return applicationRepository.uploadApplicationDocuments(uris = uris)
    }
}