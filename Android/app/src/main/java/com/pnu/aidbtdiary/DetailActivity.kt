package com.pnu.aidbtdiary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pnu.aidbtdiary.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        val date = intent.getStringExtra("date")
        binding.tvDate.text = date

        // TODO: DB에서 해당 날짜 일기 불러와서 각 TextView에 표시
    }
}