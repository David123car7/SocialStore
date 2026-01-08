package com.ipca.socialstore.domain.services.pdfBox

import android.content.Context
import com.ipca.socialstore.data.enums.DocumentStatus
import com.ipca.socialstore.data.enums.DocumentType
import com.ipca.socialstore.data.enums.StorageBucket
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationDocumentModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.models.DocumentStateModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ScholarshipModel
import com.ipca.socialstore.data.pdfbox.PdfGenerator
import com.ipca.socialstore.data.repository.ApplicationDocumentRepository
import com.ipca.socialstore.data.repository.ApplicationDocumentTypeRepository
import com.ipca.socialstore.data.repository.DocumentRepository
import com.ipca.socialstore.data.repository.DocumentStateRepository
import com.ipca.socialstore.data.repository.DonationRepository
import com.ipca.socialstore.data.repository.StorageRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GenerateDocumentService @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val storageRepository: StorageRepository,
    private val applicationDocumentRepository: ApplicationDocumentRepository,
    private val documentStateRepository: DocumentStateRepository,
    private val appDocTypeRepository: ApplicationDocumentTypeRepository,
    private val pdfGenerator: PdfGenerator){
    suspend operator fun invoke(
        context: Context,
        application: ApplicationModel,
        academicData: AcademicModel?,
        scholarShip: ScholarshipModel?,
    ): ResultWrapper<Unit>{
        val fileName = "temp_app_${application.id}.pdf"
        val folderName = DocumentType.REQUERIMENT.folderName

        val documentGeneratedResult = pdfGenerator.generateAndUploadPdf(
            context = context,
            fileName = fileName,
            application = application,
            academicData = academicData,
            scholarShip = scholarShip
        )
        if(documentGeneratedResult is ResultWrapper.Error)
            return ResultWrapper.Error(documentGeneratedResult.error)
        val documentBytes = (documentGeneratedResult as ResultWrapper.Success).data
        val uploadDocumentResult = storageRepository.uploadApplicationDocumentBytes(
            bytes = documentBytes,
            fileName = fileName,
            folderName = folderName,
            context = context
        )
        if(uploadDocumentResult is ResultWrapper.Error)
            return ResultWrapper.Error(uploadDocumentResult.error)
        val filePath = (uploadDocumentResult as ResultWrapper.Success).data

        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val currentDate = LocalDate.now().format(formatter)
        val document = DocumentModel(
            path = filePath,
            folderName = folderName,
            name = fileName,
            createdAt = currentDate
        )

        val appDocTypeResult = appDocTypeRepository.getApplicationDocumentTypeId(
            applicationId = application.id!!,
            DocumentType.REQUERIMENT.folderName
        )
        if(appDocTypeResult is ResultWrapper.Error) return ResultWrapper.Error(appDocTypeResult.error)
        val appDocTypeId = (appDocTypeResult  as ResultWrapper.Success).data

        val documentResult = documentRepository.createDocument(document = document)
        if(documentResult is ResultWrapper.Error) return ResultWrapper.Error(documentResult.error)
        val documentId = (documentResult  as ResultWrapper.Success).data

        val documentState = DocumentStateModel(state = DocumentStatus.NO_STATUS.status, description = "")
        val documentStateResult = documentStateRepository.createDocumentState(documentState = documentState)
        if(documentStateResult is ResultWrapper.Error) return ResultWrapper.Error(documentStateResult.error)
        val documentStateId = (documentStateResult  as ResultWrapper.Success).data

        val applicationDocument = ApplicationDocumentModel(appDocTypeId = appDocTypeId, stateId = documentStateId, documentId = documentId)
        val appDocumentResult = applicationDocumentRepository.createApplicationDocument(applicationDoc = applicationDocument)
        if(appDocumentResult is ResultWrapper.Error) return ResultWrapper.Error(appDocumentResult.error)

        return ResultWrapper.Success(Unit)
    }
}