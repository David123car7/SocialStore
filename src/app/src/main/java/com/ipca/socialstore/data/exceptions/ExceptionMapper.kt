package com.ipca.socialstore.data.exceptions

import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.serialization.SerializationException
import java.net.UnknownHostException
import javax.inject.Inject

class ExceptionMapper @Inject constructor(private val fileLogger: FileLogger) {
    fun map(e: Throwable): AppError {
        return when (e) {
            //api errors
            is RestException -> {
                when (e.statusCode) {
                    400 -> AppError.InvalidPassword // Bad Request
                    404 -> AppError.UserNotFound
                    409 -> AppError.UserAlreadyExists
                    else -> {
                        fileLogger.logError("SERVER_ERROR", e)
                        AppError.UnknownError("Server error: ${e.statusCode}")
                    }
                }
            }

            //network errors
            is UnknownHostException, is HttpRequestTimeoutException -> {
                AppError.NetworkError
            }

            //dev errors (code is broken)
            is SerializationException -> {
                fileLogger.logError("PARSE_ERROR", e)
                AppError.ParseError
            }

            else -> {
                fileLogger.logError("CRASH", e)
                AppError.UnknownError(e.message ?: "Unknown")
            }
        }
    }
}