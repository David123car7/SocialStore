package com.ipca.socialstore.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
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

class ApplicationRepository @Inject constructor(private val supabaseClient: SupabaseClient,
     private val exceptionMapper: ExceptionMapper,
     @ApplicationContext private val context: Context){

    suspend fun uploadApplicationDocuments(uris: List<Uri>): ResultWrapper<List<String>>{
        val uploadedPaths = mutableListOf<String>()

        return try {
            for (uri in uris) {
                val result = uploadApplicationDocument(uri)

                when (result) {
                    is ResultWrapper.Success -> {
                        if (result.data != null) {
                            uploadedPaths.add(result.data)
                        } else {
                            return ResultWrapper.Error(AppError.UnknownError("Upload success but path missing"))
                        }
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


    suspend fun uploadApplicationDocument(uri: Uri): ResultWrapper<String> {
        return try {
            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: return ResultWrapper.Error(AppError.UserNotFound)

            // 2. Read the File Data (Run on IO Thread)
            val bytes = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.readBytes()
                }
            } ?: return ResultWrapper.Error(AppError.UnknownError("Could not read file"))

            val fileName = "${UUID.randomUUID()}.pdf"
            val filePath = "$userId/$fileName"

            val bucket = supabaseClient.storage.from(StorageBucket.APPLICATION_DOCUMENTS.bucketName)

            val x = bucket.upload(path = filePath, data = bytes) {
                upsert = false // Don't overwrite existing files
            }

            ResultWrapper.Success(filePath)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}