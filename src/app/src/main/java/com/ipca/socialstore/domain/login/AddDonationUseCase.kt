package com.ipca.socialstore.domain.login

import com.ipca.socialstore.Models.DonationModel
import com.ipca.socialstore.data.repository.DonationsRepository
import com.ipca.socialstore.data.repository.ResultWrapper
import javax.inject.Inject

class AddDonationUseCase @Inject constructor(private val donationsRepository: DonationsRepository){
     operator fun invoke(donation : DonationModel?) : ResultWrapper<Boolean>{
         if (donation == null) return ResultWrapper.Error("donation object null")
        return donationsRepository.addDonation(donation = donation)
    }
}