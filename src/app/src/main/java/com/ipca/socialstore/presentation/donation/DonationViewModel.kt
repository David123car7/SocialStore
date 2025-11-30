package com.ipca.socialstore.presentation.donation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipca.socialstore.data.models.DonationModel
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import com.ipca.socialstore.domain.donation.CreateDonationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DonationState(
    val error : String? = null,
    val isLoading : Boolean? = null,
    val donation : DonationModel = DonationModel("","","")
)
@HiltViewModel
class DonationViewModel @Inject constructor(private val createDonationUseCase: CreateDonationUseCase) : ViewModel(){

    val uiState = mutableStateOf(DonationState())


    fun updateDonationDate(date : String){

    }

    fun createDonation(){
    uiState.value = uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = createDonationUseCase(donation = uiState.value.donation)
            when(result){
                is ResultWrapper.Success -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                    )
                }
                is ResultWrapper.Error -> {
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
}