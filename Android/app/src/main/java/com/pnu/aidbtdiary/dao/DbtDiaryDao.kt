package com.pnu.aidbtdiary.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pnu.aidbtdiary.entity.DbtDiary
import java.time.LocalDate

@Dao
interface DbtDiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg diary: DbtDiary)

    @Query("SELECT * FROM dbt_diary")
    suspend fun getAll(): List<DbtDiary>

    @Query("SELECT * FROM dbt_diary WHERE date = :date")
    suspend fun getByDate(date: LocalDate): DbtDiary?

    @Query("SELECT * FROM dbt_diary WHERE date >= :startDate AND date <= :endDate")
    suspend fun getAllBetweenDates(startDate: LocalDate, endDate: LocalDate): List<DbtDiary>

    @Update
    suspend fun update(vararg diary: DbtDiary)
}