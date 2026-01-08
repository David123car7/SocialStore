package com.ipca.socialstore.data.enums

import kotlinx.serialization.SerialName

enum class DocumentType(val folderName: String) {
    PERMANENT_EXPENSES("expenses"),
    INCOME_PROOF("income"),
    BANK_STATEMENTS("bank"),
    OTHER_INCOME("other_income"),
    INTERNATIONAL_SUPPORT("international"),

    REQUERIMENT("requeriment"),
}