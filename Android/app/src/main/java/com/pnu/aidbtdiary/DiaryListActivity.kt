package com.pnu.aidbtdiary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.pnu.aidbtdiary.adapter.CalendarAdapter
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.databinding.ActivityDiaryListBinding
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import com.pnu.aidbtdiary.model.Sentiment
import com.pnu.aidbtdiary.utils.CalendarUtils
import java.time.YearMonth

class DiaryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryListBinding
    private lateinit var dao : DbtDiaryDao

    private var currentYearMonth: YearMonth = YearMonth.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = AppDatabaseHelper.getDatabase(applicationContext).dbtDiaryDao()

        binding.btnBack.setOnClickListener {
            finish()
        }

        // 월 표시
        updateMonthText()

        // 월 이동 버튼
        binding.btnPrevMonth.setOnClickListener {
            currentYearMonth = currentYearMonth.minusMonths(1)
            updateMonthText()
            loadCalendar()
        }
        binding.btnNextMonth.setOnClickListener {
            currentYearMonth = currentYearMonth.plusMonths(1)
            updateMonthText()
            loadCalendar()
        }

        // 달력 로드
        loadCalendar()
    }

    private fun updateMonthText() {
        binding.tvMonth.text = "${currentYearMonth.year}년 ${currentYearMonth.monthValue}월"
    }

    private fun loadCalendar() {
        // TODO: 실제 DB에서 해당 월의 일기/감정 데이터 불러오기
        // 예시용 임시 데이터
        val diaryMap = mapOf(
            java.time.LocalDate.of(currentYearMonth.year, currentYearMonth.monthValue, 3) to Sentiment.POSITIVE,
            java.time.LocalDate.of(currentYearMonth.year, currentYearMonth.monthValue, 7) to Sentiment.NEGATIVE,
            java.time.LocalDate.of(currentYearMonth.year, currentYearMonth.monthValue, 14) to Sentiment.POSITIVE
        )
        val days = CalendarUtils.getMonthDays(currentYearMonth.year, currentYearMonth.monthValue, diaryMap)
        val adapter = CalendarAdapter(days) { day ->
            day.date?.let {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("date", it.toString())
                startActivity(intent)
            }
        }
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)
        binding.calendarRecyclerView.adapter = adapter
    }
}