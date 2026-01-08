package com.ipca.socialstore.domain.donation_item

import com.ipca.socialstore.data.repository.DonationItemRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetAllItemsByDonationIdUseCase @Inject constructor(
    private val donationItemRepository: DonationItemRepository
) {
    suspend operator fun invoke(donationId : Int) : ResultWrapper<List<Int>>{
        return donationItemRepository.getAllItemsByDonationId(donationId)
    }
}