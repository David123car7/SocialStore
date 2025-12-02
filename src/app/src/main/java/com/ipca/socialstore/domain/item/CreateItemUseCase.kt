package com.ipca.socialstore.domain.item

import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.models.ItemModelCreation
import com.ipca.socialstore.data.repository.ItemRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateItemUseCase @Inject constructor(private val itemRepository: ItemRepository){
    suspend operator fun invoke(item : ItemModelCreation): ResultWrapper<ItemModel>{
        return when(val searchItem = itemRepository.getItemByName(item.name)){
            is ResultWrapper.Success -> {
                searchItem
            }

            is ResultWrapper.Error ->{
                itemRepository.createItem(item = item)
            }
        }
    }
}