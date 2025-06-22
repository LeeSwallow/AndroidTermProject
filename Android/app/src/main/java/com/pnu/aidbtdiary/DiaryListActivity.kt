package com.pnu.aidbtdiary

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.pnu.aidbtdiary.adapter.CalendarAdapter
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.databinding.ActivityDiaryListBinding
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import com.pnu.aidbtdiary.helper.FsHelper
import com.pnu.aidbtdiary.utils.CalendarUtils
import kotlinx.coroutines.launch
import java.time.YearMonth

class DiaryListActivity : BaseActivity() {
    private lateinit var binding: ActivityDiaryListBinding
    private lateinit var dao : DbtDiaryDao

    private val fsHelper by lazy { FsHelper(this) }
    private val jsonFilePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            fsHelper.importDbtDiaryListFromJson(this, uri, onSuccess = { diaries ->
                lifecycleScope.launch {
                    fsHelper.syncToLocal(diaries, onSuccess = {
                        Toast.makeText(this@DiaryListActivity, "파일 읽기 성공: ${diaries.size}개의 일기가 동기화 되었습니다.", Toast.LENGTH_SHORT).show()
                        loadCalendar() // 새로고침
                    }, onError = {
                        Toast.makeText(this@DiaryListActivity, "파일 읽기 실패: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                    })
                }
            }, onError = {
                Toast.makeText(this, "파일 읽기 실패: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private var currentYearMonth: YearMonth = YearMonth.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

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

        binding.btnLoadJson.setOnClickListener {
            jsonFilePicker.launch(arrayOf("application/json"))
        }

        // 달력 로드
        loadCalendar()
    }

    private fun updateMonthText() {
        binding.tvMonth.text = "${currentYearMonth.year}년 ${currentYearMonth.monthValue}월"
    }

    private fun loadCalendar() {
        lifecycleScope.launch {
            val diaries = dao.getAllBetweenDates(
                java.time.LocalDate.of(currentYearMonth.year, currentYearMonth.monthValue, 1),
                java.time.LocalDate.of(currentYearMonth.year, currentYearMonth.monthValue, currentYearMonth.lengthOfMonth())
            )
            val days = CalendarUtils.getMonthDays(currentYearMonth.year, currentYearMonth.monthValue, diaries)
            val adapter = CalendarAdapter(days) { day ->
                day.date?.let {
                    val intent = Intent(this@DiaryListActivity, DetailActivity::class.java)
                    intent.putExtra("date", it.toString())
                    startActivity(intent)
                }
            }
            binding.calendarRecyclerView.layoutManager = GridLayoutManager(this@DiaryListActivity, 7)
            binding.calendarRecyclerView.adapter = adapter
        }
    }
}

