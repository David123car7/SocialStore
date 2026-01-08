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

fun getRequestTypeDbValue(displayLabel: String): String {
    // Handle empty or default cases
    if (displayLabel.isBlank() || displayLabel == "Nenhum") return ""

    return displayLabel.split(",") // 1. Split by comma
        .map { it.trim() }         // 2. Remove whitespace (e.g., " Limpeza" -> "Limpeza")
        .mapNotNull { label ->
            // 3. Find the Enum that matches this label
            RequestType.entries.find { it.label.equals(label, ignoreCase = true) }?.code
        }
        .joinToString("_")         // 4. Join codes with underscore
}