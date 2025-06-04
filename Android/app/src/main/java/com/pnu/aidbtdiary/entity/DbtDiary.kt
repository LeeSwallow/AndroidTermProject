package com.pnu.aidbtdiary.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "dbt_diary")
data class DbtDiary (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
     val date: LocalDate,
     val situation: String,
     val emotion: String,
     val intensity: Int,
     val thought: String,
     val behavior: String,
     val dbtSkill: String,
    val sentiment: Boolean,
    val isSynced: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)