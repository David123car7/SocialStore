package com.ipca.socialstore.data.room.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ipca.socialstore.data.models.AcademicModel

@Entity(tableName = "academic_table")
data class AcademicEntity(
    @PrimaryKey val id: Int,
    val typeCourse: String,
    val course: String,
    val studenNumber: String
)

fun AcademicEntity.toModel(): AcademicModel {
    return AcademicModel(
        id = this.id,
        typeCourse = this.typeCourse,
        course = this.course,
        studenNumber = this.studenNumber
    )
}

fun AcademicModel.toEntity(): AcademicEntity {
    return AcademicEntity(
        id = this.id ?: 0,
        typeCourse = this.typeCourse,
        course = this.course,
        studenNumber = this.studenNumber
    )
}