package com.ipca.socialstore.domain.logic

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.StockHelper
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemsFromStockUseCase
import com.ipca.socialstore.domain.stock.GetFullStockUseCase
import javax.inject.Inject

class ListAllItemsStockUseCase @Inject constructor(
    private val exceptionMapper: ExceptionMapper,
    private val getFullStockUseCase: GetFullStockUseCase,
    private val getItemsFromStockUseCase: GetItemsFromStockUseCase
){
    suspend operator fun invoke() : ResultWrapper<List<StockHelper>>{

        return try {
            var currentStock : List<StockModel>? = null

            val stockResult = getFullStockUseCase()
            when(stockResult){
                is ResultWrapper.Success ->{
                    currentStock = stockResult.data
                }

                is ResultWrapper.Error ->{
                    return ResultWrapper.Error(stockResult.error)
                }
            }


            val listItem = mutableListOf<Int>()
            if (stockResult.data != null){
                for (item in stockResult.data){
                    listItem.add(item.itemId)
                }
            }
            val items = getItemsFromStockUseCase(listItem)
            when(items){
                is ResultWrapper.Success -> {
                    println(items)
                    items
                }

                is ResultWrapper.Error -> {
                    return ResultWrapper.Error(items.error)
                }
            }


            val auxList = mutableListOf<StockHelper>()

            if (currentStock != null && items.data != null) {
                for (stock in currentStock){
                    var itemResult : ItemModel? = null

                    for (item in items.data){
                        if (stock.itemId  == item.itemId){
                            itemResult = item

                            break

                        }
                    }
                    if (itemResult != null){

                        var existHelper: StockHelper? = null
                        // ve se o item ja esta na lista de StockHelper
                        for (helper in auxList){
                            if (helper.item.itemId == itemResult.itemId){
                                existHelper = helper
                                break
                            }
                        }
                        if (existHelper != null){
                            //caso item ja tenha um stockHelper(2 data de validade)
                            //adicona nova data e quantidade ao map do helper
                            existHelper.expirationDate[stock.quantity] = stock.expirationDate
                        }else{
                            //cria um novo stockHelper para o item
                            val dateMap = mutableMapOf<Int, String>()
                            dateMap[stock.quantity] = stock.expirationDate

                            auxList.add(
                                StockHelper(
                                    item = itemResult,
                                    quantity = stock.quantity,
                                    expirationDate = dateMap
                                )
                            )
                        }
                    }
                }
            }

            val finalList = mutableListOf<StockHelper>()

            val checkId = mutableListOf<Int>()

            for (item in auxList){

                val currentId = item.item.itemId

                if (checkId.contains(currentId)){
                    continue
                }

                var quantity = 0
                var date = mutableMapOf<Int, String>()
                for (item2 in auxList){
                    if (currentId == item2.item.itemId){
                        quantity += item2.quantity

                    }
                }
                checkId.add(currentId)

                finalList.add(
                    StockHelper(
                        item = item.item,
                        quantity = quantity,
                        expirationDate = item.expirationDate
                    )
                )
            }
            ResultWrapper.Success(finalList)

        }catch (e : Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }

    }


}