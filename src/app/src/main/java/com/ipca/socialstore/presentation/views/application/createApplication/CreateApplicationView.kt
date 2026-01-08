package com.ipca.socialstore.presentation.views.application.createApplication

import ApplicationForm
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.ipca.socialstore.data.enums.RequestType
import com.ipca.socialstore.data.enums.TypeCourse
import com.ipca.socialstore.data.enums.UserRole
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.presentation.routes.DefaultRoutes
import com.ipca.socialstore.presentation.routes.GeneralRoutes
import com.ipca.socialstore.presentation.ui.components.SocialStoreDropdown
import com.ipca.socialstore.presentation.ui.components.TextFieldDateComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldStringComponent
import com.ipca.socialstore.presentation.ui.components.TextFieldValueComponent
import com.ipca.socialstore.presentation.ui.theme.GreenIPCA
import com.ipca.socialstore.presentation.ui.theme.SocialStoreTheme
import com.ipca.socialstore.presentation.utils.navigation.NavigationLogic
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateApplicationView(modifier: Modifier, navController: NavController, userRole: UserRole){
    val applicationViewModel: CreateApplicationViewModel = hiltViewModel()
    val uiState by applicationViewModel.uiState

    CreateApplicationViewContent(
        modifier = modifier,
        uiState = uiState,
        onNameUpdate = applicationViewModel::updateName,
        onYearUpdate = applicationViewModel::updateSchoolYear,
        onBirthDateUpdate = applicationViewModel::updateBirthDate,
        onCcUpdate = applicationViewModel::updateCc,
        onPhoneUpdate = applicationViewModel::updatePhoneNumber,
        onRequestTypeUpdate = applicationViewModel::updateRequestType,
        onOffCountryUpdate = applicationViewModel::updateOffCountry,
        onIsStudentUpdate = applicationViewModel::updateIsStudent,
        onTypeCourseUpdate = applicationViewModel::updateTypeCourse,
        onCourseUpdate = applicationViewModel::updateCourse,
        onStudentNumberUpdate = applicationViewModel::updateStudentNumber,
        onSubmitApplication = applicationViewModel::createApplication,
        onFaesUpdate = applicationViewModel::updateFaes,
        onScholarShipValueUpdate = applicationViewModel::updateScholarShipValue,
        onScholarShipUpdate = applicationViewModel::updateScholarShip
    )

    LaunchedEffect(uiState.isSuccess) {
        if(uiState.isSuccess){
            NavigationLogic.navigateTo(
                navController = navController,
                userRole = userRole,
                route = GeneralRoutes.Home
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateApplicationViewContent(
    modifier: Modifier,
    uiState: CreateApplicationState,
    onIsStudentUpdate: (Boolean) -> Unit,
    onNameUpdate: (String) -> Unit,
    onYearUpdate: (String) -> Unit,
    onBirthDateUpdate: (String) -> Unit,
    onCcUpdate: (String) -> Unit,
    onPhoneUpdate: (String) -> Unit,
    onRequestTypeUpdate: (String) -> Unit,
    onOffCountryUpdate:(Boolean) -> Unit,
    onFaesUpdate:(Boolean) -> Unit,
    onScholarShipUpdate:(Boolean)-> Unit,
    onScholarShipValueUpdate:(String) -> Unit,
    onTypeCourseUpdate: (String) -> Unit,
    onCourseUpdate: (String) -> Unit,
    onStudentNumberUpdate: (String) -> Unit,
    onSubmitApplication: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ApplicationForm(
            modifier = Modifier,
            name = uiState.application.name,
            birthDate = uiState.application.birthDate,
            cc = uiState.application.cc,
            phoneNumber = uiState.application.phoneNumber,
            schoolYear = if (uiState.application.schoolYear == 0) "" else uiState.application.schoolYear.toString(),
            requestType = uiState.application.requestType,
            academicCourseType = uiState.academicData?.typeCourse ?: "",
            academicCourseName = uiState.academicData?.course ?: "",
            academicStudentNumber = uiState.academicData?.studenNumber ?: "",
            scholarShipValue = if (uiState.scholarShip?.value == 0f) "" else uiState.scholarShip?.value.toString(),
            onNameChange = onNameUpdate,
            onBirthDateChange = onBirthDateUpdate,
            onCcChange = onCcUpdate,
            onPhoneChange = onPhoneUpdate,
            onYearChange = onYearUpdate,
            onRequestTypeChange = onRequestTypeUpdate,
            onAcademicTypeChange = onTypeCourseUpdate,
            onAcademicCourseChange = onCourseUpdate,
            onAcademicNumberChange = onStudentNumberUpdate,
            onIsStudentUpdate = {value -> onIsStudentUpdate(value)},
            onOffCountryUpdate = onOffCountryUpdate,
            onScholarShipValueUpdate = onScholarShipValueUpdate,
            onFaesUpdate = onFaesUpdate,
            onScholarShipUpdate = onScholarShipUpdate
        )
        Button(
            onClick = onSubmitApplication,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = buttonColors(GreenIPCA)
        ) {
            Text("Continuar", fontWeight = FontWeight.Bold)
        }
        if (uiState.error != null) {
            Text(text = uiState.error.asString(), modifier = Modifier.padding(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateApplicationViewPreview() {
    SocialStoreTheme {
        val uiState = CreateApplicationState(
            isLoading = false,
            error = null,
            isSuccess = false,
            application = ApplicationModel(
                stateId = -1,
                schoolYear = 0,
                name = "",
                birthDate = "",
                cc = "",
                phoneNumber = "",
                email = "",
                requestType = "",
                academicId = null,
                createdAt = "",
                dataStateId = -1,
                offCountry = false,
                faes = false,
                scholarshipId = -1
            ),
            academicData = AcademicModel(typeCourse = "", course = "", studenNumber = "")
        )
        CreateApplicationViewContent(
            modifier = Modifier,
            uiState = uiState,
            onNameUpdate = {},
            onYearUpdate = {},
            onBirthDateUpdate = {},
            onCcUpdate = {},
            onPhoneUpdate = {},
            onRequestTypeUpdate = {},
            onOffCountryUpdate = {},
            onFaesUpdate = {},
            onScholarShipValueUpdate = {},
            onScholarShipUpdate = {},
            onIsStudentUpdate = {},
            onTypeCourseUpdate = {},
            onCourseUpdate = {},
            onStudentNumberUpdate = {},
            onSubmitApplication = {}
        )
    }
}