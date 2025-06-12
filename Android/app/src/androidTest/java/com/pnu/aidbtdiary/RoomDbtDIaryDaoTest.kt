package com.pnu.aidbtdiary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pnu.aidbtdiary.dao.AppDatabase
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.entity.DbtDiary
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.text.insert

@RunWith(AndroidJUnit4::class)
class RoomDbtDIaryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: DbtDiaryDao

}
