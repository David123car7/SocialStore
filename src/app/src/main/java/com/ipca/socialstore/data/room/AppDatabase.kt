package com.ipca.socialstore.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ipca.socialstore.data.room.entitys.AcademicEntity
import com.ipca.socialstore.data.room.entitys.BeneficiaryEntity
import com.ipca.socialstore.data.room.interfaces.AcademicInterface
import com.ipca.socialstore.data.room.interfaces.BeneficiaryInterface

@Database(
    entities = [BeneficiaryEntity::class, AcademicEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun beneficiaryInterface(): BeneficiaryInterface
    abstract fun academicInterface(): AcademicInterface
}