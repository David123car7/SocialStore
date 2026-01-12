package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class NotificationBeneficiaryModel(
    val id: Int? = null,
    @SerialName("tittle")
    val tittle: String,
    @SerialName("description")
    val description: String,
    @SerialName("sended")
    val sended: Boolean,
    @SerialName("created_at")
    val created_at: String,
    @SerialName("beneficiary_id")
    val benificiaryId: Int,
)