package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.PostgrestQueryBuilder
import io.github.jan.supabase.postgrest.query.request.SelectRequestBuilder
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

    suspend fun getAllBeneficiary() : ResultWrapper<List<BeneficiaryModel>>{
        return try {
            val result = supabase.from(DatabaseTables.BENEFICIARY)
                .select()
                .decodeList<BeneficiaryModel>()
            ResultWrapper.Success(result)
        }catch (e : Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getListBeneficiaryById(id : List<Int>) : ResultWrapper<List<BeneficiaryModel>>{
        return try {
            val result = supabase.from(DatabaseTables.BENEFICIARY)
                .select {
                    filter {
                        isIn("id",id)
                    }
                }
                .decodeList<BeneficiaryModel>()
            ResultWrapper.Success(result)
        }catch (e : Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getBeneficiaryById(id : Int) : ResultWrapper<BeneficiaryModel>{
        return try {
            val result = supabase.from(DatabaseTables.BENEFICIARY)
                .select {
                    filter {
                        eq("id",id)
                    }
                }
                .decodeSingleOrNull<BeneficiaryModel>()
            if (result == null) {
                return ResultWrapper.Error(error = AppError.DataNotFound)
            }
            ResultWrapper.Success(result)
        }catch (e : Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }



}

