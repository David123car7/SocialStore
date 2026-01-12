package com.ipca.socialstore.data.room.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ipca.socialstore.data.models.BeneficiaryModel

@Entity(tableName = "beneficiary_table")
data class BeneficiaryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val phoneNumber: String,
    val birthDate: String,
    val academicId: Int?
)

fun BeneficiaryModel.toEntity() = BeneficiaryEntity(id!!, name, phoneNumber, birthDate, academicId)
fun BeneficiaryEntity.toModel() = BeneficiaryModel(id, name, phoneNumber, birthDate, academicId, missedAppointments = 0)