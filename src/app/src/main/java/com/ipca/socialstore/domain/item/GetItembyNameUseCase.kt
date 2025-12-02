package com.ipca.socialstore.domain.item

import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.repository.ItemRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetItemByNameUseCase @Inject constructor(private val itemRepository: ItemRepository){
    suspend operator fun invoke(itemName : String): ResultWrapper<ItemModel>{
        return itemRepository.getItemByName(itemName = itemName)
    }
}