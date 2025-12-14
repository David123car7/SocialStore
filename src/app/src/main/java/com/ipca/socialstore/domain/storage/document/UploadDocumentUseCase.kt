package com.ipca.socialstore.domain.storage.document

import android.content.Context
import android.net.Uri
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.repository.StorageRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class UploadDocumentUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private  val storageRepository: StorageRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository){
    suspend operator fun invoke(uri: Uri, folderName: String, context: Context) : ResultWrapper<Int>{
        val uidResult = authRepository.getUserUid()
        if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)
        val uid = (uidResult as ResultWrapper.Success).data

        val applicationIdResult = userRepository.getUserApplicationId(uid = uid)
        if (applicationIdResult is ResultWrapper.Error) return ResultWrapper.Error(AppError.ApplicationDontExists)
        val applicationId = (applicationIdResult as ResultWrapper.Success).data

        val uploadResult = storageRepository.uploadApplicationDocument(uri = uri, folderName, context = context)
        if(uploadResult is ResultWrapper.Error)
            return ResultWrapper.Error(uploadResult.error)
        val path = (uploadResult as ResultWrapper.Success).data

        val pathArrayList = path.split("/")
        val document = DocumentModel(path = path, name = pathArrayList[2], status = "To Review", folderName = folderName,applicationId = applicationId)

        return documentRepository.createDocument(document = document)
    }
}