package com.pnu.aidbtdiary.helper

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.dao.LocalDateTimeConverters
import com.pnu.aidbtdiary.entity.DbtDiary

class AppDatabaseHelper {
    @Database(entities = [DbtDiary::class], version = 1)
    @TypeConverters(LocalDateTimeConverters::class)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun dbtDiaryDao(): DbtDiaryDao
    }
}