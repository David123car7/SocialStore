package com.ipca.socialstore.domain.campaign

import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.repository.CampaignRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class  CreateCampaignUseCase @Inject constructor(private val campaignRepository: CampaignRepository){
    suspend operator fun invoke(campaign : CampaignModel): ResultWrapper<Boolean>{
        return campaignRepository.createCampaign(campaign = campaign)
    }
}