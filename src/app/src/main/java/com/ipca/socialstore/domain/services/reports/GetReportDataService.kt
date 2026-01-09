package com.ipca.socialstore.domain.services.reports

import android.util.Log
import com.ipca.socialstore.data.enums.DeliveryState
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.repository.DeliveryItemsRepository
import com.ipca.socialstore.data.repository.DeliveryRepository
import com.ipca.socialstore.data.repository.StockRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.item.GetItemsFromStockUseCase
import com.ipca.socialstore.domain.stock.GetFullStockUseCase
import com.ipca.socialstore.presentation.models.ReportsHelperModel
import com.ipca.socialstore.presentation.models.StockReceiverModel
import javax.inject.Inject

class GetReportDataService @Inject constructor(
    private val stockRepository: StockRepository,
    private val getItemsFromStockUseCase: GetItemsFromStockUseCase,
    private val deliveryRepository: DeliveryRepository,
    private val deliveryItemsRepository: DeliveryItemsRepository
) {
    suspend operator fun invoke(): ResultWrapper<List<ReportsHelperModel>>{
        val listStockResult = stockRepository.getFullStock()
        if(listStockResult is ResultWrapper.Error) return ResultWrapper.Error(listStockResult.error)
        val listStock = (listStockResult as ResultWrapper.Success).data
        Log.d("App Debug", "List Stock")

        //Gets Item and total amount of it
        val listItemId = listStock.map { it.itemId }
        val itemsResult = getItemsFromStockUseCase(listItemId)
        if (itemsResult is ResultWrapper.Error) return ResultWrapper.Error(itemsResult.error)
        val itemsList = (itemsResult as ResultWrapper.Success).data
        Log.d("App Debug", "List Item")

        val listDeliveriesResult = deliveryRepository.getAllDeliveriesByState(state = DeliveryState.DELIVERED.state)
        if(listDeliveriesResult is ResultWrapper.Error) return ResultWrapper.Error(listDeliveriesResult.error)
        val listDeliveries = (listDeliveriesResult as ResultWrapper.Success).data
        val listDeliveriesId = listDeliveries.mapNotNull { it.id }
        Log.d("App Debug", "List Delivieres")

        val listItemDeliveriesResult = deliveryItemsRepository.getDeliveryItemsByDeliveryIds(deliveryIds = listDeliveriesId)
        if(listItemDeliveriesResult is ResultWrapper.Error) return ResultWrapper.Error(listItemDeliveriesResult.error)
        val listItemDeliveries = (listItemDeliveriesResult as ResultWrapper.Success).data
        Log.d("App Debug", "List Item Delivieres")

        var reportsList = mutableListOf<ReportsHelperModel>()
        itemsList.forEach { item ->
            val stocks = listStock.filter { it.itemId == item.id }
            var qnt: Int = 0
            var deliveredQnt: Int = 0
            stocks.forEach { stock ->
                qnt += stock.quantity
                var deliverieItens = listItemDeliveries.filter { it.stockId ==  stock.id}
                deliverieItens.forEach {  deliverieItem ->
                    deliveredQnt += deliverieItem.quantity
                }
            }
            reportsList.add(
                ReportsHelperModel(
                    name = item.name,
                    stock = qnt,
                    delivered = deliveredQnt,
                    total = qnt + deliveredQnt
                )
            )
        }

        return ResultWrapper.Success(reportsList)
    }
}