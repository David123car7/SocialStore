package com.ipca.socialstore.presentation.models

class DocumentReceiverModel(
    val id: Int? = null,
    val stateId: Int,
    val applicationId: Int,
    val appDocId: Int,
    val path: String,
    val name: String,
    val folderName: String,
    val createdAt: String,
    val state: String,
    val description: String,
)