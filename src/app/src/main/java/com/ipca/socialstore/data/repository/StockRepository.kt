package com.ipca.socialstore.data.repository

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.enums.UnknownError
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.views.item.ExpirationDate
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

import javax.inject.Inject

class StockRepository @Inject constructor(private val supabase: SupabaseClient, private val exceptionMapper: ExceptionMapper ){

    suspend fun createStock(stock : StockModel) : ResultWrapper<Int> {
        return try {
            val stockResult = supabase.from(DatabaseTables.STOCK)
                .insert(stock){
                    select(columns = Columns.list("id"))
                }.decodeSingleOrNull<TableIdModel>()
            if(stockResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
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
                }.decodeSingleOrNull<StockModel>()
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
                    filter { eq("id", stockId) }
                    select()
                }.decodeSingleOrNull<StockModel>()
            if(stockResult == null) return ResultWrapper.Error(AppError.DataNotUpdated)
            if(stockResult.id == null) return ResultWrapper.Error(AppError.UnknownError(UnknownError.NULL_ID.errorMessage))
            ResultWrapper.Success(stockResult.id)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateQuantityInStockByDate(itemId: Int, expirationDate: String, newQuantity: Int): ResultWrapper<Int> {
        return try {
            val stockResult = supabase.from(DatabaseTables.STOCK)
                .update(mapOf("quantity" to newQuantity)) {
                    filter {
                        eq("item_id", itemId)
                        eq("expiration_date", expirationDate)
                    }
                    select()
                }.decodeSingleOrNull<StockModel>()

            if (stockResult == null) return ResultWrapper.Error(AppError.DataNotUpdated)

            val resultId = stockResult.id ?: return ResultWrapper.Error(
                AppError.UnknownError(UnknownError.NULL_ID.errorMessage)
            )

            ResultWrapper.Success(resultId)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    @OptIn(SupabaseExperimental::class)
    fun getFullStock(): Flow<ResultWrapper<List<StockModel>>> {
        return supabase
            .from(DatabaseTables.STOCK)
            .selectAsFlow(StockModel::id)
            .map { stockList ->
                if (stockList.isEmpty()) {
                    ResultWrapper.Error(AppError.DataNotFound)
                } else {
                    ResultWrapper.Success(stockList)
                }
            }
            .catch { e ->
                emit(ResultWrapper.Error(exceptionMapper.map(e)))
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
                    }.decodeList<ItemModel>()
                result.addAll(items)
            }
            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun removeStock(stockId : Int): ResultWrapper<Boolean>{
        return try {
            val removeResult = supabase.from(DatabaseTables.STOCK)
                .delete{
                    filter {
                        eq("id", stockId)
                    }
                }
            ResultWrapper.Success(true)
        }catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun addStock(stock : StockModel) : ResultWrapper<Int>{
        return try {
            val result = supabase.from(DatabaseTables.STOCK)
                .insert(stock){
                    select()
                }.decodeSingle<TableIdModel>()
            ResultWrapper.Success(result.id)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun createItemStock(itemId: Int, list : List<ExpirationDate>) : ResultWrapper<Boolean>{
        return try {
            val stocks = list.filter { values ->
                values.date.isNotBlank() && values.quantity.isNotBlank()
            }.map { value ->
                    StockModel(
                        itemId = itemId,
                        expirationDate = value.date,
                        quantity = value.quantity.toInt()
                    )
                }
            val result = supabase.from(DatabaseTables.STOCK)
                .insert(stocks)
            ResultWrapper.Success(true)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun workerExpirationDate(): ResultWrapper<List<Int>> {
        // ALTERAÇÃO AQUI: O Supabase (tipo DATE) exige yyyy-MM-dd
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val calendarStart = Calendar.getInstance()
            val startDate = formatter.format(calendarStart.time)

            val calendarEnd = Calendar.getInstance()
            calendarEnd.add(Calendar.DAY_OF_YEAR, 20)
            val endDate = formatter.format(calendarEnd.time)

            Log.d("WORKER_TEST", "Query enviada ao Supabase: $startDate até $endDate")

            val result = supabase.from(DatabaseTables.STOCK)
                .select(columns = Columns.list("id", "expiration_date")) {
                    filter {
                        and {
                            gte("expiration_date", startDate)
                            lte("expiration_date", endDate)
                        }
                    }
                }.decodeList<TableIdModel>()

            val ids = result.map { it.id.toInt() }
            Log.d("WORKER_TEST", "IDs encontrados: $ids")

            ResultWrapper.Success(ids)
        } catch (e: Exception) {
            // Agora o erro 'out of range' será capturado aqui se o formato estiver errado
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getStockById(stockId : Int) : ResultWrapper<List<StockModel>> {
        return try {
            val result = supabase.from(DatabaseTables.STOCK)
                .select {
                    filter {
                        eq("id", stockId)
                    }
                }.decodeList<StockModel>()

            ResultWrapper.Success(result)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}

