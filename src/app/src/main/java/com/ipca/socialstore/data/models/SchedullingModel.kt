package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchedulingModel(

    val id: Int? = null,

    @SerialName("scheduling_date")
    val schedulingDate : String,

    @SerialName("beneficiary_id")
    val beneficiaryId : Int,

    @SerialName("state")
    val state : String,

    @SerialName("reason")
    val reason : String?,

    @SerialName("note")
    val note : String?

)

