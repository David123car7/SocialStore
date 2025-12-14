package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class BeneficiaryRepository  @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun existBeneficiary(beneficiaryId : String) : ResultWrapper<Int> {
        return try {
            val beneficiaryResult = supabase.from(DatabaseTables.BENEFICIARY)
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("id", beneficiaryId)
                    }
                }
                .decodeSingleOrNull<TableIdModel>()
            if (beneficiaryResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(beneficiaryResult.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}

