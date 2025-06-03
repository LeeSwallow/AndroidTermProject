package com.pnu.aidbtdiary

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.entity.DbtDiary
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomDbtDIaryDaoTest {

        private lateinit var db: AppDatabaseHelper
        private lateinit var dao: DbtDiaryDao

        @Before
        fun setUp() {
            db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabaseHelper::class.java
            ).build()
            dao = db.dbtDiaryDao()
        }

        @After
        fun tearDown() {
            db.close()
        }

        @Test
        fun testInsertAndGetAll() = runBlocking {
            val diary1 = DbtDiary(date = "2024-06-01" /*, 나머지 필드 값 */)
            val diary2 = DbtDiary(date = "2024-06-02" /*, 나머지 필드 값 */)
            dao.insert(diary1, diary2)
            val all = dao.getAll()
            Assert.assertEquals(2, all.size)
        }

        @Test
        fun testGetByDate() = runBlocking {
            val diary = DbtDiary(date = "2024-06-10" /*, 나머지 필드 값 */)
            dao.insert(diary)
            val loaded = dao.getByDate("2024-06-10")
            Assert.assertNotNull(loaded)
            Assert.assertEquals("2024-06-10", loaded?.date)
        }

        @Test
        fun testGetAllBetweenDates() = runBlocking {
            val diary1 = DbtDiary(date = "2024-06-01" /*, 나머지 필드 값 */)
            val diary2 = DbtDiary(date = "2024-06-05" /*, 나머지 필드 값 */)
            val diary3 = DbtDiary(date = "2024-06-10" /*, 나머지 필드 값 */)
            dao.insert(diary1, diary2, diary3)
            val between = dao.getAllBetweenDates("2024-06-01", "2024-06-05")
            Assert.assertEquals(2, between.size)
        }

        @Test
        fun testUpdate() = runBlocking {
            val diary = DbtDiary(date = "2024-06-15" /*, 나머지 필드 값 */)
            dao.insert(diary)
            val loaded = dao.getByDate("2024-06-15")
            Assert.assertNotNull(loaded)
            val updated = loaded!!.copy(/* 수정할 필드 값 할당 */)
            dao.update(updated)
            val loadedUpdated = dao.getByDate("2024-06-15")
            // 수정된 필드 값이 맞는지 검증
            // Assert.assertEquals(수정값, loadedUpdated?.필드명)
        }
}