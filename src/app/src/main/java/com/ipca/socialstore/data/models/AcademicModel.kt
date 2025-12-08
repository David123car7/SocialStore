package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcademicModel (

    val id: Int? = null,

    @SerialName("type_course")
    val typeCourse: String,

    @SerialName("course")
    val course: String,

    @SerialName("student_number")
    val studenNumber: String,
)
