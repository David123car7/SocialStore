package com.ipca.socialstore.data.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole(val value: String) {
    @SerialName("admin")
    ADMIN("admin"),

    @SerialName("beneficiary")
    BENEFICIARY("beneficiary"),

    @SerialName("default")
    DEFAULT("default"),

    @SerialName("norole")
    NOROLE("norole")
}
