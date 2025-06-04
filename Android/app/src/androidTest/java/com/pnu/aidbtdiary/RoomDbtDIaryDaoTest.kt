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
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.text.insert

@RunWith(AndroidJUnit4::class)
class RoomDbtDIaryDaoTest {

    private lateinit var db: AppDatabaseHelper.AppDatabase
    private lateinit var dao: DbtDiaryDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabaseHelper.AppDatabase::class.java
        ).build()
        dao = db.dbtDiaryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun createDiary(
        date: LocalDate,
        situation: String = "상황",
        emotion: String = "감정",
        intensity: Int = 5,
        thought: String = "생각",
        behavior: String = "행동",
        dbtSkill: String = "스킬",
        isSynced: Boolean = false,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    ) = DbtDiary(
        date = date,
        situation = situation,
        emotion = emotion,
        intensity = intensity,
        thought = thought,
        behavior = behavior,
        dbtSkill = dbtSkill,
        isSynced = isSynced,
        createdAt = createdAt,
        updatedAt = updatedAt,
        sentiment = false // Default sentiment value
    )

    val diary1 = createDiary(
        date = LocalDate.of(2024, 6, 1),
        situation = "학교에서 친구와 대화",
        emotion = "기쁨",
        intensity = 7,
        thought = "오늘은 좋은 하루야",
        behavior = "웃음",
        dbtSkill = "마음챙김",
        isSynced = true,
        createdAt = LocalDateTime.of(2024, 6, 1, 8, 0),
        updatedAt = LocalDateTime.of(2024, 6, 1, 9, 0)
    )
    val diary2 = createDiary(
        date = LocalDate.of(2024, 6, 2),
        situation = "시험 결과 발표",
        emotion = "불안",
        intensity = 5,
        thought = "더 열심히 공부해야겠다",
        behavior = "한숨",
        dbtSkill = "감정조절",
        isSynced = false,
        createdAt = LocalDateTime.of(2024, 6, 2, 10, 0),
        updatedAt = LocalDateTime.of(2024, 6, 2, 11, 0)
    )

    @Test
    fun testInsertAndGetAll() = runBlocking {

        dao.insert(diary1, diary2)
        val all = dao.getAll()
        println("All diaries: $all")
        Assert.assertEquals(2, all.size)
    }

    @Test
    fun testGetByDate() = runBlocking {
        val diary = createDiary(LocalDate.of(2024, 6, 10))
        dao.insert(diary)
        val loaded = dao.getByDate(LocalDate.of(2024, 6, 10))
        Assert.assertNotNull(loaded)
        Assert.assertEquals(LocalDate.of(2024, 6, 10), loaded?.date)
    }

    @Test
    fun testGetAllBetweenDates() = runBlocking {
        val diary1 = createDiary(LocalDate.of(2024, 6, 1))
        val diary2 = createDiary(LocalDate.of(2024, 6, 5))
        val diary3 = createDiary(LocalDate.of(2024, 6, 10))
        dao.insert(diary1, diary2, diary3)
        val between = dao.getAllBetweenDates(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 5))
        Assert.assertEquals(2, between.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val diary = createDiary(LocalDate.of(2024, 6, 15), emotion = "기쁨")
        dao.insert(diary)
        val loaded = dao.getByDate(LocalDate.of(2024, 6, 15))
        Assert.assertNotNull(loaded)
        val updated = loaded!!.copy(emotion = "슬픔")
        dao.update(updated)
        val loadedUpdated = dao.getByDate(LocalDate.of(2024, 6, 15))
        Assert.assertEquals("슬픔", loadedUpdated?.emotion)
    }
}
