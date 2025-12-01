package com.ipca.socialstore.data.exceptions

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileLogger @Inject constructor(@ApplicationContext private val context: Context) {

    // The file will be saved in /data/data/com.ipca.socialstore/files/app_logs.txt
    private val logFile: File
        get() = File(context.filesDir, "app_logs.txt")

    fun logError(tag: String, e: Throwable) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

        val stackTrace = e.stackTraceToString()

        val logMessage = """
            |TIME: $timestamp
            |TAG:  $tag
            |ERROR: ${e.message}
            |STACKTRACE:
            |$stackTrace
            |--------------------------------------------------
            |
        """.trimMargin()

        try {
            synchronized(this) {
                logFile.appendText(logMessage)
            }
        } catch (ioException: Exception) {
            ioException.printStackTrace()
        }
    }

    fun getAllLogs(): String {
        return if (logFile.exists()) {
            logFile.readText()
        } else {
            "No logs found."
        }
    }

    fun clearLogs() {
        if (logFile.exists()) {
            logFile.delete()
        }
    }
}