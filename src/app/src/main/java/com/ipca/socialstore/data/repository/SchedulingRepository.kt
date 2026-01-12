package com.ipca.socialstore.data.repository

import android.util.Log
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
import io.github.jan.supabase.postgrest.query.Count
import java.util.Calendar
import javax.inject.Inject

class SchedulingRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val exceptionMapper: ExceptionMapper)
{

    suspend fun createScheduling(scheduling : SchedulingModel) : ResultWrapper<SchedulingModel> {
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .insert(scheduling) {
                    select()
                }.decodeSingle<SchedulingModel>()

            ResultWrapper.Success(result)
        } catch (e: Exception) {
            Log.e("App Debug", "KAZZIO Erro no Insert: ${e.message}")
            e.printStackTrace()
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
            supabase.from(DatabaseTables.SCHEDULING)
                .update(
                    {
                        set("reason", newReason)
                        set("state", "justified")
                    }
                ) {
                    filter { eq("id", schedulingId) }
                }

            val updatedItem = supabase.from(DatabaseTables.SCHEDULING)
                .select() {
                    filter { eq("id", schedulingId) }
                }.decodeSingle<SchedulingModel>()

            ResultWrapper.Success(updatedItem)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateNote(schedulingId: Int, note: String): ResultWrapper<SchedulingModel> {
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .update(
                    {
                        set("note", note)
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

    suspend fun acceptSchedulingDate(schedulingId : Int, note: String) : ResultWrapper<SchedulingModel>{
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .update(
                    {
                        set("state", "accept")
                        set("note", note)
                        set("notified_admin", true)
                    }
                ) {
                    filter { eq("id", schedulingId) }
                    select()
                }.decodeSingle<SchedulingModel>()

            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun declineSchedulingDate(schedulingId : Int, reason : String) : ResultWrapper<SchedulingModel>{
        return try {
            // 1. Atualiza o agendamento e obtém os dados do mesmo (incluindo o beneficiary_id)
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .update(
                    {
                        set("state", "decline")
                        set("reason", reason)
                    }
                ) {
                    filter { eq("id", schedulingId) }
                    select()
                }.decodeSingle<SchedulingModel>()

            // 2. Agora que temos o result, sabemos o ID do beneficiário
            val beneficiaryId = result.beneficiaryId

            // 3. Vamos buscar o contador atual do beneficiário
            val currentBeneficiary = supabase.from(DatabaseTables.BENEFICIARY)
                .select {
                    filter { eq("id", beneficiaryId) }
                }.decodeSingle<BeneficiaryModel>()

            // 4. Incrementamos o valor (tratando o NULL da imagem como 0)
            val newCount = (currentBeneficiary.missedAppointments ?: 0) + 1

            // 5. Atualizamos a tabela do beneficiário com o nome exato da coluna da BD
            supabase.from(DatabaseTables.BENEFICIARY)
                .update(
                    {
                        set("missed_appointments", newCount)
                    }
                ) {
                    filter { eq("id", beneficiaryId) }
                }

            // 6. Retornamos o agendamento atualizado com sucesso
            ResultWrapper.Success(result)

        } catch (e: Exception) {
            Log.e("App Debug", "Erro ao recusar agendamento: ${e.message}")
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }


    suspend fun getUnnotifiedSchedules(): ResultWrapper<List<SchedulingModel>> {
        return try {
            val result = supabase.from(DatabaseTables.SCHEDULING)
                .select{
                    filter {
                    eq("notified_admin", false)
                }
                }
                .decodeList<SchedulingModel>()
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun markAsNotified(id: Int) {
        try {
            supabase.from(DatabaseTables.SCHEDULING)
                .update(mapOf("notified_admin" to true)) {
                filter { eq("id", id) }
            }
        } catch (e: Exception) { }
    }

    suspend fun adminAcceptJustification(schedulingId: Int): ResultWrapper<SchedulingModel> {

        return try {
            val updatedScheduling = supabase.from(DatabaseTables.SCHEDULING)
                .update({
                    set("state", "justified")
                }) {
                    filter { eq("id", schedulingId) }
                    select()
                }.decodeSingle<SchedulingModel>()

            val beneficiaryId = updatedScheduling.beneficiaryId

            val beneficiary = supabase.from(DatabaseTables.BENEFICIARY)
                .select {
                    filter { eq("id", beneficiaryId) }
                }.decodeSingle<BeneficiaryModel>()

            val currentMissed = beneficiary.missedAppointments ?: 0
            val newCount = if (currentMissed > 0) currentMissed - 1 else 0
            supabase.from(DatabaseTables.BENEFICIARY)
                .update({
                    set("missed_appointments", newCount)
                }) {
                    filter { eq("id", beneficiaryId) }
                }

            ResultWrapper.Success(updatedScheduling)
        } catch (e: Exception) {
            Log.e("App Debug", "Erro ao aceitar justificação: ${e.message}")
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

}