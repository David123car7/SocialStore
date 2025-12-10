package com.ipca.socialstore.domain.item

import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.repository.ItemRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetItemIdByNameUseCase @Inject constructor(private val itemRepository: ItemRepository){
    suspend operator fun invoke(itemName : String): ResultWrapper<Int>{
        return itemRepository.getItemIdByName(name = itemName)
    }
}