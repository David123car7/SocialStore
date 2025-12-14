package com.ipca.socialstore.domain.academic

import com.ipca.socialstore.data.models.AcademicModel
import com.ipca.socialstore.data.repository.AcademicRepository
import com.ipca.socialstore.data.resultwrappers.ResultWrapper
import javax.inject.Inject

class GetAcademicDataUseCase @Inject constructor(
    private val academicRepository: AcademicRepository) {
    suspend operator fun invoke(academicId: Int): ResultWrapper<AcademicModel> {
        return academicRepository.getAcademic(academicId)
    }
}