package com.ipca.socialstore.data.enums

enum class StorageBucket(val bucketName: String) {
    APPLICATION_DOCUMENTS("application-documents"),
    PRODUCT_IMAGES("products");

    override fun toString() = bucketName
}