package com.ipca.socialstore.domain.services.application

import com.ipca.socialstore.data.repository.ApplicationDataStateRepository
import com.ipca.socialstore.data.repository.ApplicationRepository
import com.ipca.socialstore.data.repository.ApplicationStateRepository
import com.ipca.socialstore.data.repository.ScholarshipRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.ApplicationModelReceiver
import javax.inject.Inject

class GetAllAplicationsService @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val applicationStateRepository: ApplicationStateRepository,
    private val applicationDataStateRepository: ApplicationDataStateRepository,
    private val scholarshipRepository: ScholarshipRepository
) {
    suspend operator fun invoke(): ResultWrapper<List<ApplicationModelReceiver>> {
        val applicationsResult = applicationRepository.getAllApplications()
        if (applicationsResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = applicationsResult.error)
        val applications = (applicationsResult as ResultWrapper.Success).data

        val appStatesResult = applicationStateRepository.getApplicationStates(ids = applications.map { it.stateId })
        if (appStatesResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = appStatesResult.error)
        val appStates = (appStatesResult as ResultWrapper.Success).data

        val appDataStatesResult = applicationDataStateRepository.getApplicationDataStates(ids = applications.map { it.dataStateId })
        if (appDataStatesResult is ResultWrapper.Error)
            return ResultWrapper.Error(error = appDataStatesResult.error)
        val appDataStates = (appDataStatesResult as ResultWrapper.Success).data

        val statesMap = appStates.associateBy { it.id }
        val dataStatesMap = appDataStates.associateBy { it.id }

        val applicationList = applications.map { application ->
            val state = statesMap[application.stateId]
            val dataState = dataStatesMap[application.dataStateId]
            ApplicationModelReceiver(
                id = application.id,
                schoolYear = application.schoolYear,
                requestType = application.requestType,
                name = application.name,
                birthDate = application.birthDate,
                cc = application.cc,
                phoneNumber = application.phoneNumber,
                email = application.email,
                createdAt = application.createdAt,
                applicationDataState = dataState!!,
                applicationState = state!!,
                academicData = null,
                offCountry = application.offCountry,
                scholarShip = null,
                faes = application.faes
            )
        }

        return ResultWrapper.Success(applicationList)
    }
}