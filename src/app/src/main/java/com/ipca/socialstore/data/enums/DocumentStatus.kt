package com.ipca.socialstore.data.enums

enum class DocumentStatus(val status: String) {
    TO_REVIEW("to_review"),
    ACCEPTED("accepted"),
    TO_SEND("to_send"),
    DENIED("denied"),
    NO_STATUS("no_status"), //file cant be deleted
}