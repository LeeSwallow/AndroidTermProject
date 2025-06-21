package com.pnu.aidbtdiary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pnu.aidbtdiary.dao.DbtDiaryDao
import com.pnu.aidbtdiary.databinding.ActivityDetailBinding
import com.pnu.aidbtdiary.helper.AppDatabaseHelper
import kotlinx.coroutines.launch
import java.time.LocalDate

class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var dao: DbtDiaryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        dao = AppDatabaseHelper.getDatabase(this).dbtDiaryDao()
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val date = intent.getStringExtra("date")
        val localDate = LocalDate.parse(date)
        binding.tvDate.text = date

        lifecycleScope.launch {
            val diaries = dao.getAllBetweenDates(localDate, localDate)
            if (diaries.isNotEmpty()) {
                val diary = diaries[0]
                binding.tvSituation.text = diary.situation
                binding.tvEmotion.text = diary.emotion
                binding.tvIntensity.text = diary.intensity.toString()
                binding.tvThought.text = diary.thought
                binding.tvReaction.text = diary.behavior
                binding.tvUserResponse .text = diary.solution
            }
        }
        // TODO: DB에서 해당 날짜 일기 불러와서 각 TextView에 표시
    }
}