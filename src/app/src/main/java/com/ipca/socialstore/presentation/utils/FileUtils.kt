package com.ipca.socialstore.presentation.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName = "Unknown File"

    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
    }

    else {
        fileName = uri.lastPathSegment ?: "Unknown File"
    }

    return fileName
}