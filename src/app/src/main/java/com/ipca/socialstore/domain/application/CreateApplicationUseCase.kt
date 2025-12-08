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
    suspend operator fun invoke(applicationModel: ApplicationModel, academicModel: AcademicModel?): ResultWrapper<Boolean> {
        val email = authRepository.getUserEmail()
        val uid = authRepository.getUserUid()

        if(email is ResultWrapper.Error)
            return ResultWrapper.Error(email.error)

        if(uid is ResultWrapper.Error)
            return ResultWrapper.Error(uid.error)

        if(email.data == null || uid.data == null){
            return ResultWrapper.Error(AppError.UserNotLoggedIn)
        }

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

            val academic = academicRepository.createAcademic(academicModel = academicModel)
            if(academic is ResultWrapper.Error){
                return ResultWrapper.Error(academic.error)
            }

            if(academic.data == null){
                return ResultWrapper.Error(AppError.ErroCreatingTable(R.string.table_academic))
            }

            academicID = academic.data
        }

        val applicationState = applicationStateRepository.createApplicationState(applicationState = ApplicationStateModel(state = ApplicationStatus.PENDING.value))
        if(applicationState is ResultWrapper.Error)
            return ResultWrapper.Error(applicationState.error)

        if(applicationState.data == null){
            return ResultWrapper.Error(AppError.ErroCreatingTable(R.string.table_application_state))
        }

        val application = applicationRepository.createApplication(
            applicationModel.copy(stateId = applicationState.data, academicId = academicID, email = email.data)
        )

        if(application is ResultWrapper.Error)
            return ResultWrapper.Error(application.error)
        if(application.data == null)
            return ResultWrapper.Error(AppError.ErroCreatingTable(R.string.table_application))

        return userRepository.setUserApplicationId(uid = uid.data, application.data)
    }
}
