package com.ipca.socialstore.data.enums

import kotlinx.serialization.SerialName

enum class DocumentType(val folderName: String) {

    // a) Expenses
    PERMANENT_EXPENSES("expenses"),

    // b) Income Proofs
    INCOME_PROOF("income"),

    // c) Bank Statements
    BANK_STATEMENTS("bank"),

    // d) Other Income
    OTHER_INCOME("other_income"),

    // e-i & e-ii) International Docs
    INTERNATIONAL_SUPPORT("international"),
}