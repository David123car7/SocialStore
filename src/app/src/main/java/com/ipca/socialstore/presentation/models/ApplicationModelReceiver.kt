package com.ipca.socialstore.presentation.models

import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationDataStateModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.ScholarshipModel

data class ApplicationModelReceiver (
    val id: Int? = null,
    val schoolYear: Int,
    val name: String,
    val birthDate: String,
    val createdAt: String,
    val cc: String,
    val phoneNumber: String,
    val email: String,
    val requestType: String,
    val offCountry: Boolean,
    val faes: Boolean,
    val applicationState: ApplicationStateModel,
    val applicationDataState: ApplicationDataStateModel,
    val academicData: AcademicModel?,
    val scholarShip: ScholarshipModel?
)