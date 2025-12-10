package com.ipca.socialstore.domain.item

import com.ipca.socialstore.data.exceptions.AppError
import com.ipca.socialstore.data.models.ItemModel
import com.ipca.socialstore.data.repository.ItemRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class CreateItemUseCase @Inject constructor(private val itemRepository: ItemRepository){
    suspend operator fun invoke(item: ItemModel): ResultWrapper<Int> {
        return when (val searchItem = itemRepository.getItemIdByName(item.name)) {
            is ResultWrapper.Success -> {
                return searchItem
            }
            is ResultWrapper.Error -> {
                if(searchItem.error == AppError.DataNotFound)
                    itemRepository.createItem(item = item)
                else
                    return ResultWrapper.Error(searchItem.error)
            }
        }
    }
}