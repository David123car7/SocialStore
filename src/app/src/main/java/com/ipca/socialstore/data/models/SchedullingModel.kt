package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchedulingModel(

    val id: Int? = null,

    @SerialName("schedulingDate_id")
    val dateId : Int? = null,

    @SerialName("notification_Id")
    val notificationId : Int? = null,

    @SerialName("beneficiary_id")
    val beneficiaryId : Int? = null
)

