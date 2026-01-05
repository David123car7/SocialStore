package com.ipca.socialstore.data.enums

enum class ApplicationDocumentTypeState(val state: String) {
    TO_REVIEW("not_completed"),
    SOMETHING_WRONG("something_wrong"),
    COMPLETED("completed"),
}