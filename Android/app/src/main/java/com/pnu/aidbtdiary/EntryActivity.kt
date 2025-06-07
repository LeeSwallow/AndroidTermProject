package com.pnu.aidbtdiary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pnu.aidbtdiary.databinding.EntryMainBinding

class EntryActivity : AppCompatActivity() {
    private lateinit var binding: EntryMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntryMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveEntry.setOnClickListener {
            // 입력값 수집
            val situation = binding.etSituation.text.toString()
            val emotionType = binding.spEmotionType.selectedItem.toString()
            val intensity = binding.sbEmotionIntensity.progress
            val thought = binding.etThought.text.toString()
            val reaction = binding.etReaction.text.toString()

            // 저장 로직 (DB, ViewModel 등)

            // AI 조언 화면으로 전달
            val intent = Intent(this, AdviceActivity::class.java).apply {
                putExtra("situation", situation)
                putExtra("emotionType", emotionType)
                putExtra("intensity", intensity)
                putExtra("thought", thought)
                putExtra("reaction", reaction)
            }
            startActivity(intent)
            finish()
        }
    }
}