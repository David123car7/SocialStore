package com.ipca.socialstore.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.ipca.socialstore.data.enums.StorageBucket
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper,
    @ApplicationContext private val context: Context){

    suspend fun uploadApplicationDocuments(uris: List<Uri>, folderName: String, context: Context): ResultWrapper<List<String>> {
        val uploadedPaths = mutableListOf<String>()

        return try {
            for (uri in uris) {
                val result = uploadApplicationDocument(uri = uri, folderName = folderName, context = context)
                when (result) {
                    is ResultWrapper.Success -> {
                        uploadedPaths.add(result.data)
                    }
                    is ResultWrapper.Error -> {
                        //Delete all files that were stored before error
                        return ResultWrapper.Error(result.error)
                    }
                }
            }

            ResultWrapper.Success(uploadedPaths)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun uploadApplicationDocument(uri: Uri, folderName: String, context: Context): ResultWrapper<String> {
        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return ResultWrapper.Error(AppError.UserNotFound)

            // 2. Read the File Data (Run on IO Thread)
            val bytes = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.readBytes()
                }
            } ?: return ResultWrapper.Error(AppError.UnknownError("Could not read file"))

            val fileName = getFileNameFromUri(context = context, uri = uri)
            val filePath = "$userId/$folderName/$fileName"

            val bucket = supabaseClient.storage.from(StorageBucket.APPLICATION_DOCUMENTS.bucketName)

            val x = bucket.upload(path = filePath, data = bytes) {
                upsert = false // Don't overwrite existing files
            }

            ResultWrapper.Success(filePath)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun removeDocument(filePath: String): ResultWrapper<Unit>{
        return try {
            val bucket = supabaseClient.storage.from(StorageBucket.APPLICATION_DOCUMENTS.bucketName)
            bucket.delete(listOf(filePath))
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun removeDocuments(filePaths: List<String>): ResultWrapper<Unit> {
        return try {
            val bucket = supabaseClient.storage.from(StorageBucket.APPLICATION_DOCUMENTS.bucketName)
            bucket.delete(filePaths)
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result ?: "unknown_file_${UUID.randomUUID()}"
    }
}