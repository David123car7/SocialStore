package com.ipca.socialstore.data.room.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ipca.socialstore.data.room.entitys.AcademicEntity

@Dao
interface AcademicInterface {
    @Query("SELECT * FROM academic_table WHERE id = :id")
    suspend fun getAcademic(id: Int): AcademicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcademic(academic: AcademicEntity)
}