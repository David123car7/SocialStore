package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DeliveriesModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class DeliveryRepository @Inject constructor(
    private val supabase: SupabaseClient,
    val exceptionMapper: ExceptionMapper )
{
    suspend fun createDelivery(schedulingId: Int): ResultWrapper<Int> {
        return try {
            val newDelivery = DeliveriesModel(
                schedulingId = schedulingId,
                state = "por entregar"
            )

            val result = supabase.from(DatabaseTables.DELIVERY)
                .insert(newDelivery) {
                    select()
                }.decodeSingleOrNull<TableIdModel>()

            if (result != null) {
                return ResultWrapper.Success(result.id)
            }

            ResultWrapper.Error(AppError.DataNotCreated)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDeliveryBySchedulingId(schedulingId: Int): ResultWrapper<DeliveriesModel> {
        return try {
            val result = supabase.from(DatabaseTables.DELIVERY)
                .select {
                    filter {
                        eq("scheduling_id", schedulingId)
                    }
                }.decodeSingleOrNull<DeliveriesModel>()

            if (result != null) {
                ResultWrapper.Success(result)
            } else {
                ResultWrapper.Error(AppError.DataNotFound)
            }
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllDeliveriesByState(state: String) : ResultWrapper<List<DeliveriesModel>>{
        return try {
            val deliveries = supabase.from(DatabaseTables.DELIVERY)
                .select{
                    filter { eq("state", state) }
                }.decodeList<DeliveriesModel>()
            ResultWrapper.Success(deliveries)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}