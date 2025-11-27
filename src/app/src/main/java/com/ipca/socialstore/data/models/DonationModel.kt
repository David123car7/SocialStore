package com.ipca.socialstore.data.models

import java.util.Date

data class DonationModel (
    val campaign : String? = null,
    val donationData : Date? = null,
    val donatedItems : Map<String, String>? = emptyMap(),
    val donorName : String? = null
)