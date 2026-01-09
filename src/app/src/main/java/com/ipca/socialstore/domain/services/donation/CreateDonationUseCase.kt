package com.ipca.socialstore.domain.services.donation

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donation.CreateDonationUseCase
import com.ipca.socialstore.domain.donation_item.AddItemToDonationUseCase
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.item.GetItemByNameUseCase
import com.ipca.socialstore.domain.item.GetItemNameUseCase
import com.ipca.socialstore.domain.item.GetListItemByNameUseCase
import com.ipca.socialstore.domain.services.stock.CreateItemStockDonationService
import com.ipca.socialstore.domain.services.stock.CreateItemStockService
import com.ipca.socialstore.presentation.models.CreateDonationHelperModel
import com.ipca.socialstore.presentation.views.item.ExpirationDate
import javax.inject.Inject

class CreateDonationServiceUseCase @Inject constructor(
    private val getListItemByNameUseCase: GetListItemByNameUseCase,
    private val createItemUseCase: CreateItemUseCase,
    private val createDonationUseCase: CreateDonationUseCase,
    private val addItemToDonationUseCase: AddItemToDonationUseCase,
    private val createItemStockDonationService: CreateItemStockDonationService
) {
    suspend operator fun invoke(donation : DonationModel, donationHelper : List<CreateDonationHelperModel>) : ResultWrapper<Boolean>{

        val finalItemsId = mutableListOf<Int>()
        val listName = donationHelper.map { it.item.name }

        val getItemResult = getListItemByNameUseCase(listName)
        if (getItemResult is ResultWrapper.Error) return ResultWrapper.Error(getItemResult.error)
        val itemsId = (getItemResult as ResultWrapper.Success).data

        donationHelper.forEach { id ->
            val match = itemsId.find { it.name == id.item.name }

            if (match != null){
                finalItemsId.add(match.id!!)
            }
            else{
                val createItemResult = createItemUseCase(id.item)
                if (createItemResult is ResultWrapper.Error) return ResultWrapper.Error(createItemResult.error)
                val newId = (createItemResult as ResultWrapper.Success).data
                finalItemsId.add(newId)
            }
        }


        val createDonationResult = createDonationUseCase(donation)
        if (createDonationResult is ResultWrapper.Error) return ResultWrapper.Error(createDonationResult.error)
        val donation = (createDonationResult as ResultWrapper.Success).data

        finalItemsId.forEachIndexed { index ,id ->

            val match = donationHelper[index]

                val newDonationItem = DonationItemModel(
                    donationId = donation,
                    itemId = id,
                    quantity = match.quantity
                )
                addItemToDonationUseCase(newDonationItem)

            val stockResult = createItemStockDonationService(itemId = id, expirationDate = match.expirationDate, quantity = match.quantity)

            if (stockResult is ResultWrapper.Error) return ResultWrapper.Error(stockResult.error)

        }

        return ResultWrapper.Success(true)
    }
}