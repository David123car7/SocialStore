package com.ipca.socialstore.presentation.utils

import com.ipca.socialstore.data.enums.RequestType

fun updateRequestTypeString(currentString: String, typeClicked: RequestType, isChecked: Boolean): String {
    val currentCodes = if (currentString.isBlank()) {
        mutableListOf()
    } else {
        currentString.split("_").toMutableList()
    }

    if (isChecked) {
        if (!currentCodes.contains(typeClicked.label)) {
            currentCodes.add(typeClicked.code)
        }
    } else {
        currentCodes.remove(typeClicked.code)
    }

    return currentCodes.sorted().joinToString("_")
}

fun getRequestTypeDisplayLabel(dbValue: String): String {
    if (dbValue.isBlank()) return "Nenhum"

    return dbValue.split("_")
        .mapNotNull { code ->
            RequestType.entries.find { it.code == code }?.label
        }
        .joinToString(", ")
}