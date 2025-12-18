package com.ipca.socialstore.presentation.views.home.defaultHomeView

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.CampaignModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.auth.LogoutUseCase
import com.ipca.socialstore.presentation.utils.ErrorText
import com.ipca.socialstore.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DefaultHomeState (
    var error : ErrorText? = null,
    var campaignList: List<CampaignModel> = listOf(
        CampaignModel(
            id = 1,
            name = "Natal Solidário",
            description = "Ajude-nos a compor 500 cabazes para as famílias mais carenciadas da comunidade académica.",
            category = "Alimentar",
            date = "2025-12-25"
        ),
        CampaignModel(
            id = 2,
            name = "Kit Escolar 2026",
            description = "Recolha de cadernos, canetas e calculadoras para o segundo semestre.",
            category = "Educação",
            date = "2026-02-10"
        ),
        CampaignModel(
            id = 3,
            name = "Inverno Quente",
            description = "Estamos a recolher casacos e mantas em bom estado.",
            category = "Vestuário",
            date = "2025-11-30"
        )
    ),
    var isLoading : Boolean = false,
)

class DefaultHomeViewModel: ViewModel() {
    var uiState = mutableStateOf(DefaultHomeState())
}