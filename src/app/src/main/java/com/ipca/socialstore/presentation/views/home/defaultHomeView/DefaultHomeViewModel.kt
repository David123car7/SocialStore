package com.ipca.socialstore.presentation.views.home.defaultHomeView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.presentation.utils.errors.ErrorText

data class DefaultHomeState (
    var error : ErrorText? = null,
    var campaignList: List<CampaignModel> = listOf(
        CampaignModel(
            id = 1,
            name = "Natal Solidário",
            description = "Ajude-nos a compor 500 cabazes para as famílias mais carenciadas da comunidade académica.",
            category = "Alimentar",
            onGoing = true,
            startDate = "",
            endDate = ""
        ),
        CampaignModel(
            id = 2,
            name = "Kit Escolar 2026",
            description = "Recolha de cadernos, canetas e calculadoras para o segundo semestre.",
            category = "Educação",
            onGoing = true,
            startDate = "",
            endDate = ""
        ),
        CampaignModel(
            id = 3,
            name = "Inverno Quente",
            description = "Estamos a recolher casacos e mantas em bom estado.",
            category = "Vestuário",
            onGoing = true,
            startDate = "",
            endDate = ""
        )
    ),
    var isLoading : Boolean = false,
)

class DefaultHomeViewModel: ViewModel() {
    var uiState = mutableStateOf(DefaultHomeState())
}