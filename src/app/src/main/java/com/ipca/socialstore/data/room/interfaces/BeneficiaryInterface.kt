package com.ipca.socialstore.data.room.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ipca.socialstore.data.room.entitys.BeneficiaryEntity

@Dao
interface BeneficiaryInterface {
    @Query("SELECT * FROM beneficiary_table WHERE id = :id")
    suspend fun getBeneficiary(id: Int): BeneficiaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(beneficiary: BeneficiaryEntity)

    @Query("DELETE FROM beneficiary_table")
    suspend fun clearAll()
}