package com.pnu.aidbtdiary.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_setting")
data class CloudSetting(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    var isEnabled: Boolean = false
)