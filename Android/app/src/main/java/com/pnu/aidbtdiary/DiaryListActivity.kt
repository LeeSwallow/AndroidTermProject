package com.pnu.aidbtdiary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pnu.aidbtdiary.databinding.ActivityDiaryListBinding

class DiaryListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("date", dateString)
            startActivity(intent)
        }

//        binding.rvDiarySummary.layoutManager = LinearLayoutManager(this)
        // TODO: 어댑터 설정하여 간단 요약(긍정/부정 아이콘 등) 표시

    }
}