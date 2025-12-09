package com.ipca.socialstore.data.repository

import android.content.Context
import android.net.Uri
import com.ipca.socialstore.R
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.StorageBucket
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ApplicationModel
import com.ipca.socialstore.data.models.ApplicationStateModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ApplicationRepository @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val exceptionMapper: ExceptionMapper){

    suspend fun createApplication(application: ApplicationModel): ResultWrapper<Int>{
        return try {
            val application = supabaseClient.from(DatabaseTables.APPLICATION).insert(application){
                select()
            }.decodeList<ApplicationModel>().firstOrNull()

            if(application == null)
                return ResultWrapper.Error(AppError.ErroCreatingTable(R.string.table_application))

            if(application.id == null)
                return ResultWrapper.Error(AppError.UnknownError("Database returned null ID"))

            ResultWrapper.Success(application.id)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}