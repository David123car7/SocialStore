package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.DeliveryItemsModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class DeliveryItemsRepository @Inject constructor(
    private val supabase: SupabaseClient,
    val exceptionMapper: ExceptionMapper
) {

    suspend fun createDeliveryItems(deliveryItems : List<DeliveryItemsModel>) : ResultWrapper<Int>{
        return try {
            val result = supabase.from(DatabaseTables.DELIVERY_ITEMS)
                .insert(deliveryItems){
                    select()
                }.decodeList<TableIdModel>()

            if (result.isNotEmpty()){
                return ResultWrapper.Success(deliveryItems.first().deliveryId)
            }

            ResultWrapper.Error(AppError.DataNotCreated)
        } catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getDeliveryItemsByDeliveryId(deliveryId: Int): ResultWrapper<List<DeliveryItemsModel>> {
        return try {
            val result = supabase.from(DatabaseTables.DELIVERY_ITEMS)
                .select {
                    filter {
                        eq("delivery_id", deliveryId)
                    }
                }
                .decodeList<DeliveryItemsModel>()
            if (result.isNotEmpty()) {
                ResultWrapper.Success(result)
            } else {
                ResultWrapper.Error(AppError.DataNotFound)
            }
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateDeliveryItems(items: List<DeliveryItemsModel>): ResultWrapper<Unit> {
        return try {
            supabase.from(DatabaseTables.DELIVERY_ITEMS).upsert(items)
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun deleteDeliveryItems(ids: List<Int>): ResultWrapper<Unit> {
        return try {
            supabase.from(DatabaseTables.DELIVERY_ITEMS).delete {
                filter {
                    isIn("id", ids)
                }
            }
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}