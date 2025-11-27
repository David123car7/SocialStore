package com.ipca.socialstore.domain.donations

import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.data.repository.DonationsRepository
import javax.inject.Inject

class AddDonationUseCase @Inject constructor(private val donationsRepository: DonationsRepository){
     suspend operator fun invoke(donation : DonationModel?) : ResultWrapper<Boolean> {
         if (donation == null) return ResultWrapper.Error("donation object null")
        return donationsRepository.addDonation(donation = donation)
    }
}