package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import java.util.Calendar
import javax.inject.Inject

class SchedulingRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val exceptionMapper: ExceptionMapper)
{

    suspend fun createScheduling(scheduling : SchedulingModel) : ResultWrapper<Int> {
        return try {
            val scheduling = supabase.from(DatabaseTables.SCHEDULING)
                .insert(scheduling) {
                    select(columns = Columns.list("id"))
                }.decodeAsOrNull<TableIdModel>()
            if (scheduling == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(scheduling.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getSchedulingByMonth(month: Int, year: Int): ResultWrapper<List<SchedulingModel>> {
        val monthStr = month.toString().padStart(2, '0')
        val firstDay = "$year-$monthStr-01"

        val nextMonth = if (month == 12) 1 else month + 1
        val nextYear = if (month == 12) year + 1 else year
        val nextMonthStr = nextMonth.toString().padStart(2, '0')
        val firstDayNextMonth = "$nextYear-$nextMonthStr-01"

        return try {
            val scheduling = supabase.from(DatabaseTables.SCHEDULING)
                .select {
                    filter {
                        and {
                            gte("scheduling_date", firstDay)
                            lt("scheduling_date", firstDayNextMonth)
                            neq("state", "canceled")
                        }

                    }
                }
                .decodeList<SchedulingModel>()

            println("Query rigorosa: $firstDay até < $firstDayNextMonth")
            println(scheduling)
            ResultWrapper.Success(scheduling)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun cancelScheduling(id: Int): ResultWrapper<Boolean> {
        return try {
            supabase.from(DatabaseTables.SCHEDULING)
                .update(
                    {
                        set("state", "canceled")
                    }
                ) {
                    filter {
                        eq("id", id)
                    }
                }
            ResultWrapper.Success(true)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getSchedulingByBeneficiaryId(id: Int): ResultWrapper<List<SchedulingModel>>{
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .select{
                    filter {
                        eq("beneficiary_id", id)
                    }
                }
            val response = result.decodeList<SchedulingModel>()
            ResultWrapper.Success(response)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateReason(schedulingId: Int, newReason: String): ResultWrapper<SchedulingModel> {
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .update(
                    {
                        set("reason", newReason)
                        set("state", "justified")
                    }
                ) {
                    filter {
                        eq("id", schedulingId)
                    }
                }.decodeSingle<SchedulingModel>()

            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getBeneficiaryBySchedulingId(schedulingId : Int) : ResultWrapper<SchedulingModel>{
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .select {
                    filter {
                        eq("id",schedulingId )
                    }
                }.decodeSingle<SchedulingModel>()
            ResultWrapper.Success(result)
        }catch (e : Exception){
            return ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getSchedulingById(id: Int): ResultWrapper<SchedulingModel>{
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .select{
                    filter {
                        eq("id", id)
                    }
                }
            val response = result.decodeSingle<SchedulingModel>()
            ResultWrapper.Success(response)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }


}