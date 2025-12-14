package com.ipca.socialstore.domain.services

import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donation_item.AddItemToDonationUseCase
import com.ipca.socialstore.domain.donation.CreateDonationUseCase
import com.ipca.socialstore.domain.donation.GetDonationByIdUseCase
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.item.GetItemByIdUseCase
import com.ipca.socialstore.domain.stock.AddItemStockUseCase
import javax.inject.Inject

class AddDonationService @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val getItemByIdUseCase: GetItemByIdUseCase,
    private val addItemStockUseCase: AddItemStockUseCase,
    private val createDonationUseCase: CreateDonationUseCase,
    private val getDonationByIdUseCase: GetDonationByIdUseCase,
    private val addItemToDonationUseCase: AddItemToDonationUseCase,
    private val exceptionMapper: ExceptionMapper

){
    suspend operator fun invoke(item: ItemModel,  donation: DonationModel, expirationDate: String, quantity: Int): ResultWrapper<Int> {

        return try {
            //Creates Item
            val createItemResult = createItemUseCase(item)
            if(createItemResult is ResultWrapper.Error)
                return ResultWrapper.Error(createItemResult.error)
            val itemId = (createItemResult as ResultWrapper.Success).data

            //Gets Item
            val itemResult = getItemByIdUseCase(itemId = itemId)
            if(itemResult is ResultWrapper.Error)
                return ResultWrapper.Error(itemResult.error)
            val item = (itemResult as ResultWrapper.Success).data

            //Creates Donation
            val createDonationResult = createDonationUseCase(donation)
            if(createDonationResult is ResultWrapper.Error)
                return ResultWrapper.Error(createDonationResult.error)
            val donationId = (createDonationResult as ResultWrapper.Success).data

            //Gets Donation
            val donationResult = getDonationByIdUseCase(id = donationId)
            if(donationResult is ResultWrapper.Error)
                return ResultWrapper.Error(donationResult.error)
            val donation = (donationResult as ResultWrapper.Success).data


            val stockResult = addItemStockUseCase(itemId = itemId, expirationDate = expirationDate, quantity = quantity)
            if(stockResult is ResultWrapper.Error)
                return ResultWrapper.Error(stockResult.error)
            val stock = (stockResult as ResultWrapper.Success).data


            // Relacionar cada Item á sua doacao (Rever isto)
            var currentDonationItem : DonationItemModel = DonationItemModel(itemId = 0, donationId = 0)
            if (item.id != null && donation.id != null){
                val donationItem = DonationItemModel(
                    itemId = itemId,
                    donationId = donation.id
                )
                currentDonationItem = donationItem
            }

            return addItemToDonationUseCase(currentDonationItem)
        }catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }




    }
}


