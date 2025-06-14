package com.pnu.aidbtdiary.utils
import com.pnu.aidbtdiary.model.DiaryDay
import com.pnu.aidbtdiary.model.Sentiment
import java.time.LocalDate

object CalendarUtils {
    fun getMonthDays(year: Int, month: Int, diaryMap: Map<LocalDate, Sentiment?>): List<DiaryDay> {
        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())
        val startDayOfWeek = firstDay.dayOfWeek.value % 7 // 일요일=0
        val days = mutableListOf<DiaryDay>()

        // 앞쪽 빈칸
        repeat(startDayOfWeek) { days.add(DiaryDay(null)) }

        // 실제 날짜
        for (d in 1..lastDay.dayOfMonth) {
            val date = LocalDate.of(year, month, d)
            val sentiment = diaryMap[date]
            days.add(DiaryDay(date, sentiment != null, sentiment))
        }

        // 7의 배수 맞추기 위한 뒤쪽 빈칸
        while (days.size % 7 != 0) days.add(DiaryDay(null))
        return days
    }
}