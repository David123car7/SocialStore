package com.ipca.socialstore.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationDocumentModel(
    val id: Int? = null,

    @SerialName("document_id")
    val documentId: Int,

    @SerialName("app_doc_type_id")
    val appDocTypeId: Int,

    @SerialName("state_id")
    val stateId: Int,
)

@Serializable
data class ApplicationDocumentStateOnly(@SerialName("state_id") val stateId: Int)

@Serializable
data class ApplicationDocumentDocOnly(@SerialName("document_id") val documentId: Int)
