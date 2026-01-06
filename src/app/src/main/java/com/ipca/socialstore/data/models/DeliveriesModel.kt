package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeliveriesModel (

    val id : Int? = null,

    @SerialName("state")
    val state : String,

    @SerialName("scheduling_id")
    val schedulingId : Int
)