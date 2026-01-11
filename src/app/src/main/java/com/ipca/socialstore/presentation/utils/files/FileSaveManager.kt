package com.ipca.socialstore.presentation.utils.files

import android.content.Context
import android.net.Uri
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSaveManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun writeDataToUri(uri: Uri, bytes: ByteArray): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(bytes)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun writeContentToUri(uri: Uri, content: String): Boolean {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }
}