package com.pnu.aidbtdiary

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pnu.aidbtdiary.dao.AppDatabase
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.entity.DbtDiary
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.text.insert

@RunWith(AndroidJUnit4::class)
class RoomDbtDIaryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: DbtDiaryDao

    @Test
    fun testDao() {
        db = AppDatabaseHelper.getDatabase(ApplicationProvider.getApplicationContext())
        dao = db.dbtDiaryDao()
        val (startMonth, endMonth) = getMonthStartEnd(2025, 6)
        GlobalScope.launch {
            val diaries = dao.getAllBetweenDates(startMonth, endMonth)
            println(diaries)
            assert(diaries.isNotEmpty());
        }
    }

     fun getMonthStartEnd(year: Int, month: Int): Pair<LocalDate, LocalDate> {
        val start = LocalDate.of(year, month, 1)
        val end = start.withDayOfMonth(start.lengthOfMonth())
        return Pair(start, end)
    }

}
