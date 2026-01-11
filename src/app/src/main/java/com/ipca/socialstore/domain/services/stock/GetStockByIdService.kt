package com.ipca.socialstore.domain.services.stock

import com.ipca.socialstore.data.email.ResendEmailService
import com.ipca.socialstore.data.models.SchedulingModel
import com.ipca.socialstore.data.repository.ItemRepository
import com.ipca.socialstore.data.repository.SchedulingRepository
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.presentation.models.StockReceiverModel
import com.ipca.socialstore.presentation.models.StockReceiverModel2
import javax.inject.Inject

class GetStockByIdService @Inject constructor(
    private val stockRepository: StockRepository,
    private val itemRepository: ItemRepository,
    private val emailService: ResendEmailService
) {
    suspend operator fun invoke(itemId: Int) : ResultWrapper<StockReceiverModel2>{
        val stockResult = stockRepository.getStockByItemId(itemId)
        if(stockResult is ResultWrapper.Error) return ResultWrapper.Error(stockResult.error)
        val stocks = (stockResult as ResultWrapper.Success).data

        val itemResult = itemRepository.getItemById(id = itemId)
        if(itemResult is ResultWrapper.Error) return ResultWrapper.Error(itemResult.error)
        val item = (itemResult as ResultWrapper.Success).data

        val mapList = mutableListOf<Map<String, Int>>()
        val idsList = mutableListOf<Int>()
        val totalQty = stocks.sumOf { it.quantity }
        stocks.forEach { stock ->
            val map = mutableMapOf<String, Int>()
            map.set(stock.expirationDate, stock.quantity)
            mapList.add(map)
            idsList.add(stock.id!!)
        }
        val stock = StockReceiverModel2(
            item = item,
            quantityMap = mapList,
            totalQuantity = totalQty,
        )

        val result = emailService.sendEmail(
            to = "david123car7@gmail.com",
            subject = "Application Status Update",
            messageBody = "Your application has been <b>ACCEPTED</b>" // This maps to 'html' in your Edge Function
        )

        return ResultWrapper.Success(stock)
    }
}