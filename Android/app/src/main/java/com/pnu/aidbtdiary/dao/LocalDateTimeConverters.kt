package com.pnu.aidbtdiary.dao

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

object LocalDateTimeConverters {
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(data: LocalDate?): Long? {
        return data?.toEpochDay()
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: Long?): LocalDate? =
        value?.let {
            LocalDate.ofEpochDay(it)
        }

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(data: LocalDateTime?): Long? {
        return data?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: Long?): LocalDateTime? =
        value?.let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
        }
}