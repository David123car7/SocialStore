package com.ipca.socialstore.domain.logic

import android.util.Log
import com.ipca.socialstore.data.models.DonationItem
import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.ItemModelCreation
import com.ipca.socialstore.data.models.StockModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donation.AddItemToDonationUseCase
import com.ipca.socialstore.domain.donation.CreateDonationUseCase
import com.ipca.socialstore.domain.item.CreateItemUseCase
import com.ipca.socialstore.domain.item.GetItemByNameUseCase
import com.ipca.socialstore.domain.stock.AddItemStockUseCase
import com.ipca.socialstore.domain.stock.GetItemByIdStock
import com.ipca.socialstore.domain.stock.UpdateStockQuantityUseCase
import javax.inject.Inject

class AddDonationLogicUseCase @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val getItemByIdStock: GetItemByIdStock,
    private val updateStockQuantityUseCase: UpdateStockQuantityUseCase,
    private val getItemByNameUseCase: GetItemByNameUseCase,
    private val addItemStockUseCase: AddItemStockUseCase,
    private val createDonationUseCase: CreateDonationUseCase,
    private val addItemToDonationUseCase: AddItemToDonationUseCase
){
    suspend operator fun invoke(item: ItemModelCreation, stock: StockModel, quantity: Int, donation: DonationModel): ResultWrapper<DonationItemModel> {
        try {
            val currentItem: ItemModel? = null
            val existItem = getItemByNameUseCase(item.name)
            when(existItem){
                is ResultWrapper.Success ->{
                    existItem.data
                }
                is ResultWrapper.Error ->{
                    val createItem = createItemUseCase(item)
                    when(createItem){
                        is ResultWrapper.Success ->{
                            createItem.data
                        }
                        is ResultWrapper.Error ->{
                            return ResultWrapper.Error(createItem.error)
                        }
                    }
                }
            }

            val stockItem = stock.copy(itemId = existItem.data?.itemId!!)

            val stockResult = when (val getStock = getItemByIdStock(existItem.data.itemId)){
                is ResultWrapper.Success ->{
                    val existStock = getStock.data
                    if(existStock != null){
                        val updateStock = existStock.copy(
                            expirationDate = stock.expirationDate
                        )
                        updateStockQuantityUseCase(updateStock,quantity)
                    }else{
                        addItemStockUseCase(stock)
                    }
                }
                is ResultWrapper.Error -> {
                    ResultWrapper.Error(getStock.error)
                }
            }
            when(stockResult){
                is ResultWrapper.Error->{
                    return ResultWrapper.Error(stockResult.error)
                }
                is ResultWrapper.Success ->{
                    val createDonation = when(val donationCreate = createDonationUseCase(donation)){
                        is ResultWrapper.Success -> donationCreate.data
                        is ResultWrapper.Error ->{
                            return ResultWrapper.Error(donationCreate.error)
                        }
                    }
                    val donationItemCreate = DonationItemModel(
                        itemId = existItem.data.itemId,
                        donationId = createDonation?.donationId ?:""
                    )
                    val createdDonationItemResult = addItemToDonationUseCase(donationItemCreate)
                    return when(createdDonationItemResult){
                        is ResultWrapper.Success ->{
                            ResultWrapper.Success(data = createdDonationItemResult.data)
                        }
                        is ResultWrapper.Error ->{
                            ResultWrapper.Error(createdDonationItemResult.error)
                        }
                    }

                }
            }

        }


        when(existItem){
            is ResultWrapper.Success ->{
                val itemFound = existItem.data

                updateStock = stock.copy(
                    itemId = itemFound?.itemId ?: "",
                    expirationDate = stock.expirationDate,
                )
                val stockResult = updateStockQuantityUseCase(updateStock,quantity)
                when(stockResult){
                    is ResultWrapper.Success ->{
                        val updateDonation = createDonationUseCase(donation)
                        when(updateDonation){
                            is ResultWrapper.Success ->{
                                donationItem.copy(
                                    itemId = itemFound?.itemId ?:"",
                                    donationId = donation.donationId ?: ""
                                )
                                val createItemDonation = addItemToDonationUseCase(donationItem!!)
                            }
                            is ResultWrapper.Error ->{
                                // Falta o erro
                            }
                        }


                    }
                    is ResultWrapper.Error ->{

                    }
                }
            }
            is ResultWrapper.Error->{

            }
        }
        return TODO("Provide the return value")
    }

}