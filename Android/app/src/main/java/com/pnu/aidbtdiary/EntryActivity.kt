package com.pnu.aidbtdiary

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.pnu.aidbtdiary.databinding.EntryMainBinding

class EntryActivity : BaseActivity() {
    private lateinit var binding: EntryMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = EntryMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

    val emotions = listOf(
        // 긍정 감정
        "행복" to "happy",
        "즐거움" to "joy",
        "감사" to "gratitude",
        "평온" to "calm",
        "자신감" to "confidence",

        // 부정 감정
        "슬픔" to "sad",
        "분노" to "angry",
        "두려움" to "fear",
        "놀람" to "surprise",
        "혐오" to "disgust",
        "지루함" to "boredom",
        "후회" to "regret",
        "수치심" to "shame",
        "실망" to "disappointment",
        "불안" to "anxiety",
        "외로움" to "loneliness",
        "질투" to "jealousy"
    )
    testInit()

    // 스피너에는 한글만 표시
    val emotionNames = emotions.map { it.first }
    binding.spEmotionType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emotionNames)
    binding.spEmotionType.setSelection(0)

    binding.btnSaveEntry.setOnClickListener {
        // 입력값 수집
        val situation = binding.etSituation.text?.toString() ?: ""
        val emotionType = emotions[binding.spEmotionType.selectedItemPosition].second
        val intensity = binding.sbEmotionIntensity.progress
        val thought = binding.etThought.text?.toString() ?: ""
        val reaction = binding.etReaction.text?.toString() ?: ""

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

    fun testInit() {
        // 테스트용 초기화 메소드
        binding.etSituation.setText("오늘은 날씨가 맑아서 기분이 좋았어요.")
        binding.spEmotionType.setSelection(0) // "행복" 선택
        binding.sbEmotionIntensity.progress = 5 // 중간 정도의 강도
        binding.etThought.setText("이런 날은 항상 행복해요.")
        binding.etReaction.setText("산책을 갔어요.")
    }
}