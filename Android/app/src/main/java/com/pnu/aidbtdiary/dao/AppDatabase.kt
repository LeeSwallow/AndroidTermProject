package com.pnu.aidbtdiary.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pnu.aidbtdiary.entity.DbtDiary

@Database(entities = [DbtDiary::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dbtDiaryDao(): DbtDiaryDao
}