package com.ipca.socialstore.domain.services.document

import android.content.Context
import android.net.Uri
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.models.DocumentStateModel
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.repository.DocumentStateRepository
import com.ipca.socialstore.data.repository.StorageRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import java.time.LocalDate
import javax.inject.Inject

class UploadDocumentsService @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val documentStateRepository: DocumentStateRepository,
    private  val storageRepository: StorageRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository){
    suspend operator fun invoke(filesList: List<Uri>, folderName: String, context: Context) : ResultWrapper<Unit>{
        val uidResult = authRepository.getUserUid()
        if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)
        val uid = (uidResult as ResultWrapper.Success).data

        val applicationIdResult = userRepository.getUserApplicationId(uid = uid)
        if (applicationIdResult is ResultWrapper.Error) return ResultWrapper.Error(AppError.ApplicationDontExists)
        val applicationId = (applicationIdResult as ResultWrapper.Success).data

        val uploadResult = storageRepository.uploadApplicationDocuments(uris = filesList, folderName, context = context)
        if(uploadResult is ResultWrapper.Error)
            return ResultWrapper.Error(uploadResult.error)
        val paths = (uploadResult as ResultWrapper.Success).data

        val documentState = DocumentStateModel(state = DocumentStatus.TO_REVIEW.status, description = "")

        val documentsList = mutableListOf<DocumentModel>()
        for(path in paths){
            val pathArrayList = path.split("/")

            val documentStateResult = documentStateRepository.createDocumentState(documentState)
            if(documentStateResult is ResultWrapper.Error)
                return ResultWrapper.Error(documentStateResult.error)
            val documentStateId = (documentStateResult as ResultWrapper.Success).data

            val document = DocumentModel(path = path, name = pathArrayList[2], folderName = folderName, stateId = documentStateId, createdAt = LocalDate.now().toString(),applicationId = applicationId)
            documentsList.add(document)
        }

        return documentRepository.createDocuments(documentsList)
    }
}