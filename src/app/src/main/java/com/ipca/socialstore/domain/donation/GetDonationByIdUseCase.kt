package com.ipca.socialstore.domain.donation

import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.repository.DonationRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetDonationByIdUseCase @Inject constructor(private val donationRepository: DonationRepository){
    suspend operator fun invoke(id: Int): ResultWrapper<DonationModel>{
        return donationRepository.getDonationById(id = id)
    }
}