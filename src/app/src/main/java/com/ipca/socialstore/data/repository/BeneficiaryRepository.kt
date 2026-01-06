package com.ipca.socialstore.data.repository

import android.util.Log
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.BeneficiaryModel
import com.ipca.socialstore.data.models.DocumentModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class BeneficiaryRepository  @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){
    suspend fun createBeneficiary(beneficiary: BeneficiaryModel): ResultWrapper<Int> {
        return try{
            val result = supabase.from(DatabaseTables.BENEFICIARY).insert(beneficiary){
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if(result == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(result.id)
        }catch (e: Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateBeneficiary(beneficiary: BeneficiaryModel): ResultWrapper<Int> {
        return try {
            val id = beneficiary.id ?: return ResultWrapper.Error(
                exceptionMapper.map(Exception("ID do beneficiário é nulo"))
            )

            val result = supabase.from(DatabaseTables.BENEFICIARY).update(beneficiary) {
                filter {
                    eq("id", id)
                }
                select(columns = Columns.list("id"))
            }.decodeSingleOrNull<TableIdModel>()
            if (result == null) return ResultWrapper.Error(AppError.DataNotUpdated) // Ou um erro equivalente
            ResultWrapper.Success(result.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

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
            Log.d("App Debug", "Error: ${e}")
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

    suspend fun getBeneficiaryNameById(id: Int) : ResultWrapper<String> {
        return try {
            val result = supabase.from(DatabaseTables.BENEFICIARY)
                .select(columns = Columns.list("name")) {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<BeneficiaryModel>()
            if (result == null) {
                return ResultWrapper.Error(error = AppError.DataNotFound)
            }
            ResultWrapper.Success(result.name)
        } catch (e : Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}

