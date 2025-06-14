package com.pnu.aidbtdiary.model

// model/DiaryDay.kt
import java.time.LocalDate

data class DiaryDay(
    val date: LocalDate?, // null이면 빈칸
    val hasDiary: Boolean = false,
    val sentiment: Sentiment? = null
)