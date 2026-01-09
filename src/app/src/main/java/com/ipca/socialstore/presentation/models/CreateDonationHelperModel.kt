package com.ipca.socialstore.presentation.models

import com.ipca.socialstore.data.models.ItemModel

class CreateDonationHelperModel (
    val item : ItemModel,
    val quantity : Int,
    val expirationDate : String
)