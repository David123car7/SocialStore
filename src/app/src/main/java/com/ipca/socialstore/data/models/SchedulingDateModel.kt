package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchedulingDateModel(

    val id : Int? = null,

    @SerialName("date")
    val date : String? = null
)