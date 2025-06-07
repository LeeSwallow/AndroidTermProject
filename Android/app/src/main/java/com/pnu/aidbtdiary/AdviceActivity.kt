package com.pnu.aidbtdiary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pnu.aidbtdiary.databinding.ActivityAdviceBinding

class AdviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // EntryActivity에서 전달된 데이터
        val situation = intent.getStringExtra("situation")
        // ... 그 외

        // TODO: OpenAI API 호출하여 조언 가져오기
        binding.tvAdviceContent.text = "여기에 AI 조언이 표시됩니다."

        binding.btnSaveResponse.setOnClickListener {
            val userResponse = binding.etUserResponse.text.toString()
            // 사용자 대응 저장

            finish()
        }
    }
}