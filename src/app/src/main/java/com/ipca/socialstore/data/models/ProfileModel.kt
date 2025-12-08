package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileModel (
    val id: Int? = null,

    @SerialName("name")
    val name: String,

    @SerialName("birth_date") // Must be "YYYY-MM-DD"
    val birthDate: String, //supabase sends date as a string
)
