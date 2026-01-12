package com.ipca.socialstore.domain.services.application


import com.ipca.socialstore.data.enums.ApplicationStates
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.NotificationScheduledModel
import com.ipca.socialstore.data.pdfbox.PdfGenerator
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.BeneficiaryRepository
import com.ipca.socialstore.data.repository.NotificationRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.notificationSchedule.CreateInitialNotificationUseCase
import javax.inject.Inject

class AcceptApplicationService @Inject constructor(
    private val applicationStateRepository: ApplicationStateRepository,
    private val beneficiaryRepository: BeneficiaryRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository){
    suspend operator fun invoke(application: ApplicationModel): ResultWrapper<Unit>{
        val uidResult = authRepository.getUserUidByApplicationId(appId = application.id!!)
        if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)
        val uid = (uidResult as ResultWrapper.Success).data

        val appStateResult = applicationStateRepository.updateApplicationState(
            applicationState = ApplicationStateModel(
                id = application.stateId,
                state = ApplicationStates.APPROVED.status
            )
        )
        if(appStateResult is ResultWrapper.Error)
            return ResultWrapper.Error(appStateResult.error)

        val beneficiary = BeneficiaryModel(
            name = application.name,
            birthDate = application.birthDate,
            academicId = application.academicId,
            phoneNumber = application.phoneNumber
        )
        val benificiaryResult = beneficiaryRepository.createBeneficiary(beneficiary)
        if(benificiaryResult is ResultWrapper.Error)
            return ResultWrapper.Error(benificiaryResult.error)
        val benificiaryId = (benificiaryResult as ResultWrapper.Success).data

        val userRoleResult = userRepository.setUserRole(uid = uid, role = UserRole.BENEFICIARY.value)
        if(userRoleResult is ResultWrapper.Error)
            return ResultWrapper.Error(userRoleResult.error)

        val userBenIdResult = userRepository.setBeneficiaryId(uid = uid, beneficiaryId = benificiaryId)
        if(userBenIdResult is ResultWrapper.Error)
            return ResultWrapper.Error(userBenIdResult.error)

        return ResultWrapper.Success(Unit)
    }
}