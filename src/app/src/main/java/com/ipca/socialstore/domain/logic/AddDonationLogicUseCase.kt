package com.ipca.socialstore.domain.logic

import android.util.Log
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

        var currentItem: ItemModel? = null

        // 1. Tentar encontrar item existente
        val existItem = getItemByNameUseCase(item.name)
        val itemResult = existItem.data
        Log.d("DEBUG_BUSCA", "Item buscado: ${item.name}")
        Log.d("DEBUG_BUSCA", "Resultado do wrapper: ${existItem.javaClass.simpleName}")

        // Exist item
        when (existItem) {
            is ResultWrapper.Success -> {
                if (itemResult?.itemId != null) {
                    // Item ENCONTRADO (Success com data) -> Atualizar Stock ou Criar Stock
                    currentItem = itemResult
                    Log.d("Criou Item", "ItemCriado")

                    val stockResult = getItemByIdStock(itemResult.itemId)

                    when (stockResult) {
                        is ResultWrapper.Success -> {
                            // Item already on Stock -> Update Stock
                            val updateStockResult =
                                updateStockQuantityUseCase(stockResult.data!!, quantity)
                            if (updateStockResult is ResultWrapper.Error) {
                                return ResultWrapper.Error(updateStockResult.error)
                            }
                        }

                        is ResultWrapper.Error -> {
                            // Item is not in stock (GetItemByIdStock retornou Error) -> Create Stock
                            val newStock = stock.copy(
                                itemId = itemResult.itemId,
                                quantity = quantity
                            )
                            val createNewStock = addItemStockUseCase(newStock)
                            if (createNewStock is ResultWrapper.Error) {
                                return ResultWrapper.Error(createNewStock.error)
                            }
                        }
                    }
                } else {
                    // Item NÃO ENCONTRADO (Success mas data=null) -> Criar Item e Stock
                    val itemCreationResult = createItemAndStock(item, stock, quantity)
                    if (itemCreationResult is ResultWrapper.Error) {
                        return itemCreationResult
                    }
                    currentItem = (itemCreationResult as ResultWrapper.Success).data
                }
            }

            is ResultWrapper.Error -> {
                // Erro na BUSCA (ResultWrapper.Error) -> Criar Item e Stock
                val itemCreationResult = createItemAndStock(item, stock, quantity)
                if (itemCreationResult is ResultWrapper.Error) {
                    return itemCreationResult
                }
                currentItem = (itemCreationResult as ResultWrapper.Success).data
            }
        }

        // --- Lógica Centralizada de Doação e Associação ---

        // 2. Verificação e Criação da Doação
        if (currentItem?.itemId == null) {
            return ResultWrapper.Error("Item ID missing after item logic.")
        }

        val createDonation = createDonationUseCase(donation)
        Log.d("Criou Item", "Cria Doacao")

        val currentDonation: DonationModel = when (createDonation) {
            is ResultWrapper.Success -> createDonation.data
            is ResultWrapper.Error -> return ResultWrapper.Error(createDonation.error)
        }

        if (currentDonation.donationId == null) {
            return ResultWrapper.Error("Donation ID missing after creation.")
        }

        // 3. Associate item to donation
        val donationItem = DonationItemModel(
            itemId = currentItem.itemId,
            donationId = currentDonation.donationId
        )

        val addItemToDonation = addItemToDonationUseCase(donationItem)
        return when (addItemToDonation) {
            is ResultWrapper.Success -> ResultWrapper.Success(donationItem)
            is ResultWrapper.Error -> ResultWrapper.Error(addItemToDonation.error)
        }
    }

    /**
     * Função auxiliar para lidar com a criação de um novo Item e seu Stock inicial.
     * Retorna o ItemModel criado (ResultWrapper.Success<ItemModel>) ou um erro.
     */
    private suspend fun createItemAndStock(
        item: ItemModelCreation,
        stock: StockModel,
        quantity: Int
    ): ResultWrapper<ItemModel> {
        val itemCreated = createItemUseCase(item)
        Log.d("Criou Item", "ItemCriadoNoErro")

        return when (itemCreated) {
            is ResultWrapper.Success -> {
                val createdItem = itemCreated.data
                if (createdItem?.itemId == null) {
                    return ResultWrapper.Error("Item ID missing after creation.")
                }

                val newStock = stock.copy(
                    itemId = createdItem.itemId,
                    quantity = quantity
                )
                val stockCreated = addItemStockUseCase(newStock)

                return when (stockCreated) {
                    is ResultWrapper.Success -> ResultWrapper.Success(createdItem)
                    is ResultWrapper.Error -> ResultWrapper.Error(stockCreated.error)
                }
            }
            is ResultWrapper.Error -> ResultWrapper.Error(itemCreated.error)
        }
    }
}