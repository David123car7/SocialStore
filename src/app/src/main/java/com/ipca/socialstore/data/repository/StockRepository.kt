package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class StockRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper ){

    suspend fun addItemStock(item : StockModel, quantity: Int) : ResultWrapper<StockModel> {
        return try {
            val newStock = item.copy(quantity=quantity)
            val stock = supabase.from(DatabaseTables.STOCK)
                .insert(newStock){
                    select()
                }
                .decodeSingle<StockModel>()
            ResultWrapper.Success(stock)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemByIdStock(itemId : Int) : ResultWrapper<StockModel?> {
        return try {
            val stock = supabase.from(DatabaseTables.STOCK)
                .select{
                    filter {
                        eq("item_id", itemId)
                    }
                }

                .decodeSingleOrNull<StockModel>()
            ResultWrapper.Success(stock)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemQuantity(item : StockModel) : ResultWrapper<StockModel>{
        return try {
            val quantity = supabase.from(DatabaseTables.STOCK)
                .select {
                    filter {
                        eq("item_id", item.itemId!!)
                    }
            }
                .decodeSingle<StockModel>()
            ResultWrapper.Success(quantity)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
    suspend fun updateStock(item: StockModel, quantity: Int): ResultWrapper<Boolean> {
        return try {
            val getStock = getItemQuantity(item)
            val bdQuantity = getStock.data?.quantity ?: 0
            val newQuantity = bdQuantity + quantity

            val result = supabase.from(DatabaseTables.STOCK)
                .update(mapOf("quantity" to newQuantity)) {
                    filter {
                        eq("item_id", item.itemId!!)
                    }
                }
            ResultWrapper.Success(true)

        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}

