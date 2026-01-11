package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class EmailModel(
    @SerialName("to")
    val to: String,
    @SerialName("subject")
    val subject: String,
    @SerialName("html")
    val html: String
)