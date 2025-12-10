package com.ipca.socialstore.domain.donation_item

import com.ipca.socialstore.data.models.DonationItemModel
import com.ipca.socialstore.data.repository.DonationItemRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class AddItemToDonationUseCase @Inject constructor(private val  donationItemRepository: DonationItemRepository){
    suspend operator fun invoke(donationItem : DonationItemModel): ResultWrapper<Int> {
        return donationItemRepository.addItemDonation(donationItem = donationItem)
    }
}