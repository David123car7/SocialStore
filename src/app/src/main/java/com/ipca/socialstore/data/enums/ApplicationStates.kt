package com.ipca.socialstore.data.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ApplicationStatus(val status: String) {
    @SerialName("pending")
    PENDING("pending"),

    @SerialName("correction")
    CORRECTION("correction"),

    @SerialName("approved")
    APPROVED("approved"),

    @SerialName("rejected")
    REJECTED("rejected"),

    @SerialName("revoked")
    REVOKED("revoked");
}