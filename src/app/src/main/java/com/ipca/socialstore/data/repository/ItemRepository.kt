package com.ipca.socialstore.data.repository

import android.util.Log
import com.ipca.socialstore.data.enums.DatabaseTables
import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.helpers.from
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.models.TableIdModel
import com.ipca.socialstore.data.room.entitys.toEntity
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable

@Serializable
data class ItemNameModel(
    val name : String
)
class ItemRepository @Inject constructor(private val supabase : SupabaseClient, private val exceptionMapper: ExceptionMapper){

    suspend fun createItem(item : ItemModel) : ResultWrapper<Int> {
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .insert(item){
                    select(columns = Columns.list("id"))
                }
                .decodeSingleOrNull<TableIdModel>()
            if(itemResult == null) return ResultWrapper.Error(AppError.DataNotCreated)
            ResultWrapper.Success(itemResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemById(id : Int) : ResultWrapper<ItemModel>{
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .select {
                    filter {
                        eq("id",id)
                    }
                }.decodeSingleOrNull<ItemModel>()
            if(itemResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(itemResult)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun updateItem(item: ItemModel): ResultWrapper<Int> {
        if(item.id == null)
            return ResultWrapper.Error(AppError.UnknownError("Item Id Null"))
        Log.d("App Debug", "${item.id}")
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM).update(item) {
                filter {
                    eq("id", item.id)
                }
                select(columns = Columns.list("id"))
            }.decodeSingle<TableIdModel>()
            ResultWrapper.Success(item.id)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemIdByName(name : String) : ResultWrapper<Int>{
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("name",name)
                    }
                }
                .decodeSingleOrNull<TableIdModel>()
            if(itemResult == null) return ResultWrapper.Error(AppError.DataNotFound)
            ResultWrapper.Success(itemResult.id)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getListItemIdByName(name : List<String>) : ResultWrapper<List<ItemModel>>{
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .select(columns = Columns.list("id")) {
                    filter {
                        eq("name",name)
                    }
                }
                .decodeList<ItemModel>()
            ResultWrapper.Success(itemResult)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemByCode(barCode: String): ResultWrapper<ItemModel>{
        return try {
            val itemResult = supabase.from(DatabaseTables.ITEM)
                .select() {
                    filter {
                        eq("bar_code",barCode)
                    }
                }.decodeSingle<ItemModel>()
            ResultWrapper.Success(itemResult)
        }
        catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getItemName(itemId : List<Int>) : ResultWrapper<List<String>>{
        return try {
            val result = supabase.from(DatabaseTables.ITEM)
                .select(columns = Columns.list("name")){
                    filter {
                        isIn("id", itemId)
                    }
                }.decodeList<ItemNameModel>()
            val names = result.map { it.name }
            return ResultWrapper.Success(names)
        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getListItemsById(listId: List<Int>): ResultWrapper<List<ItemModel>> {
        return try {
            if (listId.isEmpty()) return ResultWrapper.Success(emptyList())
            val items = supabase.from(DatabaseTables.ITEM)
                .select {
                    filter {
                        isIn("id", listId)
                    }
                }.decodeList<ItemModel>()
            ResultWrapper.Success(items)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }

    suspend fun getAllItems(): ResultWrapper<List<ItemModel>> {
        return try {
            val items = supabase.from(DatabaseTables.ITEM)
                .select ()
                .decodeList<ItemModel>()
            ResultWrapper.Success(items)
        } catch (e: Exception) {
            ResultWrapper.Error(exceptionMapper.map(e))
        }
    }
}