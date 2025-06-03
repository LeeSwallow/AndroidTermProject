package com.pnu.aidbtdiary.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "dbt_diary")
data class DbtDiary (
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
     val date: LocalDate,
     val situation: String,
     val emotion: String,
     val intensity: Int,
     val thought: String,
     val behavior: String,
     val dbtSkill: String,
    val isSynced: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)