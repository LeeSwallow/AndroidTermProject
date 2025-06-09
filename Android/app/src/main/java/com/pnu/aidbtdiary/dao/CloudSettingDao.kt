package com.pnu.aidbtdiary.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pnu.aidbtdiary.entity.CloudSetting

@Dao
interface CloudSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(setting: CloudSetting)

    @Update
    suspend fun update(setting: CloudSetting)

    @Query("SELECT * FROM cloud_setting WHERE id = 1")
    suspend fun getSetting(): CloudSetting?
}