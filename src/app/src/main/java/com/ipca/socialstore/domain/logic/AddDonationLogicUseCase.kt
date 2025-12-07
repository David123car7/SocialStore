package com.ipca.socialstore.domain.logic

import android.util.Log
import com.ipca.socialstore.data.exceptions.ExceptionMapper
import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.DonationModelCreation
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.ItemModelCreation
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donation.AddItemToDonationUseCase
import com.ipca.socialstore.domain.donation.CreateDonationUseCase
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.stock.AddItemStockUseCase
import javax.inject.Inject

class AddDonationLogicUseCase @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val addItemStockUseCase: AddItemStockUseCase,
    private val createDonationUseCase: CreateDonationUseCase,
    private val addItemToDonationUseCase: AddItemToDonationUseCase,
    private val exceptionMapper: ExceptionMapper

){
    suspend operator fun invoke(item: ItemModelCreation, stock: StockModel, quantity: Int, donation: DonationModelCreation): ResultWrapper<DonationItemModel> {

        return try {
            var currentItem : ItemModel? = null
            val itemResult = createItemUseCase(item)
            Log.d("ITEM BASE:", "{$itemResult}")
            when(itemResult){
                is ResultWrapper.Success->{
                    currentItem = itemResult.data
                }

                is ResultWrapper.Error-> {
                    return ResultWrapper.Error(itemResult.error)
                }
            }


            //Donation
            val donationResult = createDonationUseCase(donation)
            var currentDonation : DonationModel? = null
            when(donationResult){
                is ResultWrapper.Success -> {
                    currentDonation = donationResult.data
                }
                is ResultWrapper.Error ->{
                    return ResultWrapper.Error(donationResult.error)
                }
            }

            //Add  Stock
            val newStock = stock.copy(
                itemId =  currentItem?.itemId
            )
            val stockResult = addItemStockUseCase(newStock, quantity)
            when(stockResult){
                is ResultWrapper.Success ->{
                    stockResult.data
                }
                is ResultWrapper.Error ->{
                    return ResultWrapper.Error(stockResult.error)
                }
            }


            // Relacionar cada Item á sua doacao
            var currentDonationItem : DonationItemModel = DonationItemModel(0,0)
            if (currentItem?.itemId != null && currentDonation?.donationId != null){
                val donationItem = DonationItemModel(
                    currentItem.itemId,
                    currentDonation.donationId
                )
                currentDonationItem = donationItem
            }


            val itemDonation = addItemToDonationUseCase(currentDonationItem)
            when(itemDonation){
                is ResultWrapper.Success ->{
                    return itemDonation
                }
                is ResultWrapper.Error ->{
                    return itemDonation
                }
            }
        }catch (e: Exception){
            ResultWrapper.Error(exceptionMapper.map(e))
        }




    }
}


