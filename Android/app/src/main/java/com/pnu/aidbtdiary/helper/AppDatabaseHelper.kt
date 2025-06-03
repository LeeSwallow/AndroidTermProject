package com.pnu.aidbtdiary.helper

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.entity.DbtDiary

class AppDatabaseHelper {
    @Database(entities = [DbtDiary::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun dbtDiaryDao(): DbtDiaryDao
    }
}