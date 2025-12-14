package com.ipca.socialstore.domain.application

import com.ipca.socialstore.R
import com.ipca.socialstore.data.enums.ApplicationStatus
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.repository.AcademicRepository
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.repository.AuthRepository
import com.ipca.socialstore.data.repository.UserRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateApplicationUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val academicRepository: AcademicRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val applicationStateRepository: ApplicationStateRepository) {
    suspend operator fun invoke(applicationModel: ApplicationModel, academicModel: AcademicModel?): ResultWrapper<String> {
        val emailResult = authRepository.getUserEmail()
        if (emailResult is ResultWrapper.Error) return ResultWrapper.Error(emailResult.error)

        val uidResult = authRepository.getUserUid()
        if (uidResult is ResultWrapper.Error) return ResultWrapper.Error(uidResult.error)

        val email = (emailResult as ResultWrapper.Success).data
        val uid = (uidResult as ResultWrapper.Success).data

        val userApplicationId = userRepository.getUserApplicationId(uid = uidResult.data)
        if(userApplicationId is ResultWrapper.Success)
            return ResultWrapper.Error(AppError.ApplicationAllreadyExists)

        //Application Checks
        if(applicationModel.name.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.name))

        if(applicationModel.birthDate.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.date))

        if(applicationModel.cc.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.cc))

        if(applicationModel.phoneNumber.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.phone_number))

        if(applicationModel.schoolYear == 0)
            return ResultWrapper.Error(AppError.InvalidField(R.string.schoolYear))

        if(applicationModel.requestType.isEmpty())
            return ResultWrapper.Error(AppError.EmptyField(R.string.request_type))

        //Academic Checks
        var academicID: Int? = null
        if(academicModel != null){
            if(academicModel.course.isEmpty())
                return ResultWrapper.Error(AppError.EmptyField(R.string.course))

            if(academicModel.typeCourse.isEmpty())
                return ResultWrapper.Error(AppError.EmptyField(R.string.course_type))

            if(academicModel.studenNumber.isEmpty())
                return ResultWrapper.Error(AppError.EmptyField(R.string.student_number))

            val academicResult = academicRepository.createAcademic(academicModel = academicModel)

            if(academicResult is ResultWrapper.Error) return ResultWrapper.Error(academicResult.error)
            if (academicResult is ResultWrapper.Success) academicID = academicResult.data
        }

        val applicationStateResult = applicationStateRepository.createApplicationState(applicationState = ApplicationStateModel(state = ApplicationStatus.PENDING.status))

        if(applicationStateResult is ResultWrapper.Error) return ResultWrapper.Error(applicationStateResult.error)
        val stateId = (applicationStateResult as ResultWrapper.Success).data

        val applicationResult = applicationRepository.createApplication(
            applicationModel.copy(stateId = stateId, academicId = academicID, email = email)
        )

        if(applicationResult is ResultWrapper.Error) return ResultWrapper.Error(applicationResult.error)
        val applicationId = (applicationResult as ResultWrapper.Success).data

        return userRepository.setUserApplicationId(uid = uid, id = applicationId)
    }
}
