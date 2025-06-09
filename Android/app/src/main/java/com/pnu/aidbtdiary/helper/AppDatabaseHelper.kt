// AppDatabaseHelper.kt
package com.pnu.aidbtdiary.helper

import android.content.Context
import androidx.room.Room
import com.pnu.aidbtdiary.dao.AppDatabase

object AppDatabaseHelper {
    fun getDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "aidbt-diary-db"
        ).build()
}