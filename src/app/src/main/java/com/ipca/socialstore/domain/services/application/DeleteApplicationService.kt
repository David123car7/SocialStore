package com.ipca.socialstore.domain.services.application

import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.repository.AcademicRepository
import com.ipca.socialstore.data.repository.ApplicationDocumentRepository
import com.ipca.socialstore.data.repository.ApplicationDocumentTypeRepository
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.repository.DocumentStateRepository
import com.ipca.socialstore.data.repository.StorageRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class DeleteApplicationService @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val academicRepository: AcademicRepository,
    private val applicationStateRepository: ApplicationStateRepository,
    private val appDocTypeRepository: ApplicationDocumentTypeRepository,
    private val appDocRepository: ApplicationDocumentRepository,
    private val documentRepository: DocumentRepository,
    private val documentStateRepository: DocumentStateRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
){
    suspend operator fun invoke(applicationId: Int, applicationStateId: Int, academicId: Int): ResultWrapper<Unit> {
        val uidResult = authRepository.getUserUid()
        if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)
        val uid = (uidResult as ResultWrapper.Success).data

        val appDocTypeIdsResult = appDocTypeRepository.getApplicationDocumentTypeListIds(applicationId = applicationId)
        if(appDocTypeIdsResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = appDocTypeIdsResult.error)
        val appDocTypeIds = (appDocTypeIdsResult as ResultWrapper.Success).data.map { it.id }

        val docIdsResult = appDocRepository.getDocumentsIds(appDocTypeIds = appDocTypeIds)
        if(docIdsResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = docIdsResult.error)
        val docIds = (docIdsResult as ResultWrapper.Success).data
        val docIdsList = docIds.map { it.documentId }

        if(docIdsList.isNotEmpty()){
            val docPathsResult = documentRepository.getDocumentsPaths(ids = docIdsList)
            if(docPathsResult is ResultWrapper.Error)
                return ResultWrapper.Error(error = docPathsResult.error)
            val docsPath = (docPathsResult as ResultWrapper.Success).data
            val docsPathList = docsPath.map { it.path }

            val docStateIdsResult = appDocRepository.getDocumentStatesIds(appDocTypeIds = appDocTypeIds)
            if(docStateIdsResult is ResultWrapper.Error)
                return ResultWrapper.Error(error = docStateIdsResult.error)
            val docStateIds = (docStateIdsResult as ResultWrapper.Success).data.map { it.stateId }

            val appDocIdsResult = appDocRepository.getApplicationDocuments(appDocTypeIds = appDocTypeIds)
            if(appDocIdsResult is ResultWrapper.Error)
                return ResultWrapper.Error(error = appDocIdsResult.error)
            val appDocIds = (appDocIdsResult as ResultWrapper.Success).data.map { it.id }

            val removeFilesStorage = storageRepository.removeDocuments(filePaths = docsPathList)
            if(removeFilesStorage is ResultWrapper.Error)
                return ResultWrapper.Error(error = removeFilesStorage.error)

            val removeAppDocsResult = appDocRepository.deleteApplicationDocuments(ids = appDocIds)
            if(removeAppDocsResult is ResultWrapper.Error)
                return ResultWrapper.Error(error = removeAppDocsResult.error)

            val removeDocsResult = documentRepository.deleteDocuments(ids = docIdsList)
            if(removeDocsResult is ResultWrapper.Error)
                return ResultWrapper.Error(error = removeDocsResult.error)

            val removeDocStatesResult = documentStateRepository.deleteDocumentStates(ids = docStateIds)
            if(removeDocStatesResult is ResultWrapper.Error)
                return ResultWrapper.Error(error = removeDocStatesResult.error)
        }

        val removeAppDocTypes = appDocTypeRepository.deleteApplicationDocumentTypes(ids = appDocTypeIds)
        if(removeAppDocTypes is ResultWrapper.Error)
            return ResultWrapper.Error(error = removeAppDocTypes.error)

        val setNullAppIdRes = userRepository.setUserApplicationId(uid = uid, null)
        if(setNullAppIdRes is ResultWrapper.Error)
            return ResultWrapper.Error(error = setNullAppIdRes.error)

        val removeApplicationResult = applicationRepository.deleteApplication(id = applicationId)
        if(removeApplicationResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = removeApplicationResult.error)

        val removeApplicationStateResult = applicationStateRepository.deleteApplicationState(id = applicationStateId)
        if(removeApplicationStateResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = removeApplicationStateResult.error)

        val removeAcademicResult = academicRepository.deleteAcademic(id = applicationStateId)
        if(removeAcademicResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = removeAcademicResult.error)

        val changeUserRole = userRepository.setUserRole(uid = uid, role = UserRole.DEFAULT.value)
        if(changeUserRole is ResultWrapper.Error)
            return ResultWrapper.Error(error = changeUserRole.error)

        return ResultWrapper.Success(Unit)
    }
}