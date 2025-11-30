package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    @SerialName("firstName")
    val firstName: String,

    @SerialName("lastName")
    val lastName: String,

    @SerialName("addressId")
    val addressId: Int?,

    @SerialName("birthDate") // Must be "YYYY-MM-DD"
    val birthDate: String, //supabase sends date as a string
)

fun UserModel.isValid() : Boolean{
    return this.firstName.isNotEmpty() && this.lastName.isNotEmpty() && birthDate.isNotEmpty()
}