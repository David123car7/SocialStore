package com.ipca.socialstore.data.repository

import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

class StockRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper ){

    suspend fun addStock(stock : StockModel) : ResultWrapper<Int> {
        return try {
            val stockResult = supabase.from(DatabaseTables.STOCK)
                .insert(stock){
                    select()
                }
                .decodeSingleOrNull<StockModel>()
            if(stockResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            if(stockResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(stockResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getStockByItemInfo(id: Int, expirationDate: String) : ResultWrapper<StockModel> {
        return try {
            val stockResult = supabase.from(DatabaseTables.STOCK)
                .select{
                    filter {
                        eq("item_id", id)
                        eq("expiration_date", expirationDate)
                    }
                }
                .decodeSingleOrNull<StockModel>()
            if(stockResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(stockResult)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemQuantity(itemId : Int) : ResultWrapper<Int>{
        return try {
            val stock = supabase.from(DatabaseTables.STOCK)
                .select {
                    filter {
                        eq("item_id", itemId)
                    }
                }.decodeSingleOrNull<StockModel>()
            if(stock == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(stock.quantity)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
    suspend fun updateStock(itemId: Int, quantity: Int): ResultWrapper<Int> {
        return try {
            val getStock = getItemQuantity(itemId)
            if(getStock is ResultWrapper.Error) return ResultWrapper.Error(getStock.error)
            val bdQuantity = (getStock as ResultWrapper.Success).data
            val newQuantity = bdQuantity + quantity

            val stockResult = supabase.from(DatabaseTables.STOCK)
                .update(mapOf("quantity" to newQuantity)) {
                    filter {
                        eq("item_id", itemId)
                    }
                    select()
                }.decodeSingleOrNull<StockModel>()

            if(stockResult == null) return ResultWrapper.Error(AppError.DataNotUpdated)
            if(stockResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(stockResult.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateQuantityInStock(stockId :Int, newQuantity: Int) : ResultWrapper<Int>{
        return try {
            val stockResult = supabase.from(DatabaseTables.STOCK)
                .update(mapOf("quantity" to newQuantity)) {
                    filter { eq("stock_id", stockId) }
                    select()
                }.decodeSingleOrNull<StockModel>()
            if(stockResult == null) return ResultWrapper.Error(AppError.DataNotUpdated)
            if(stockResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(stockResult.id)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getFullStock(): ResultWrapper<List<StockModel>> {
        return try {
            val stockResult = supabase
                .from(DatabaseTables.STOCK)
                .select()
                .decodeAsOrNull<List<StockModel>>()
            if(stockResult == null) return ResultWrapper.Error(AppError.DataNotUpdated)
            ResultWrapper.Success(stockResult)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun listStockToStock(list: List<StockModel>): ResultWrapper<List<ItemModel>> {
        return try {
            val result = mutableListOf<ItemModel>()

            for (stock in list) {
                val items = supabase
                    .from(DatabaseTables.ITEM)
                    .select {
                        filter {
                            eq("item_id", stock.itemId)
                        }
                    }
                    .decodeList<ItemModel>()

                result.addAll(items)
            }

            ResultWrapper.Success(result)

        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}

